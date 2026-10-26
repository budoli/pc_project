"""검색 품질 평가 (Hit@K, Recall@K, MRR).

두 가지 모드:
  api   : 실행 중인 AI 서버의 /search/similar-policies 를 그대로 호출 (현재 서비스 기준선)
  local : 설정을 바꾼 임베딩으로 eval_* 컬렉션을 따로 만들어 비교 실험 (운영 컬렉션은 건드리지 않음)

실행 (ai 디렉터리에서):
    python -m evaluation.evaluate --mode api --name baseline
    python -m evaluation.evaluate --mode local --text raw --max-seq-len 512 --name raw-512
"""
import argparse
import csv
import json
from collections import defaultdict
from datetime import datetime
from pathlib import Path

import httpx

from evaluation.db import Policy, load_policies
from evaluation.metrics import K_VALUES, evaluate_query, first_relevant_rank, mean_scores

DATA_DIR = Path(__file__).parent / "data"
RESULT_DIR = Path(__file__).parent / "results"
DEFAULT_DATASETS = [DATA_DIR / "synthetic.jsonl", DATA_DIR / "manual.jsonl"]
MAX_K = max(K_VALUES)


def load_dataset(paths: list[Path], known_ids: set[str]) -> list[dict]:
    items = []
    for path in paths:
        if not path.exists():
            print(f"dataset not found, skip: {path}")
            continue
        for line_no, line in enumerate(path.read_text(encoding="utf-8").splitlines(), start=1):
            if not line.strip():
                continue
            item = json.loads(line)
            relevant = [policy_id for policy_id in item.get("relevant", []) if policy_id in known_ids]
            missing = set(item.get("relevant", [])) - set(relevant)
            if missing:
                print(f"{path.name}:{line_no} unknown policy ids {sorted(missing)}")
            if not relevant:
                print(f"{path.name}:{line_no} no valid answer, skip")
                continue
            item["relevant"] = relevant
            item.setdefault("source", path.stem)
            items.append(item)
    return items


def build_search_query(item: dict, with_profile: bool) -> str:
    if not with_profile or not item.get("profile"):
        return item["query"]
    # 서비스와 같은 방식으로 질문 + 프로필을 하나의 쿼리로 만든다
    from domain.chat.chat_service import ChatService
    from domain.chat.schemas import UserProfile

    return ChatService(None, None, None)._build_search_query(
        item["query"], UserProfile(**item["profile"])
    )


class ApiRetriever:
    def __init__(self, api_url: str, policies: list[Policy]):
        self.url = f"{api_url.rstrip('/')}/api/v1/ai/search/similar-policies"
        self.id_to_external = {policy.id: policy.external_id for policy in policies}

    def search(self, query: str, limit: int) -> list[str]:
        response = httpx.post(self.url, json={"query": query, "limit": limit}, timeout=120.0)
        response.raise_for_status()
        return [
            self.id_to_external.get(result["policyId"], f"db:{result['policyId']}")
            for result in response.json()
        ]


class LocalRetriever:
    def __init__(self, policies: list[Policy], text: str, max_seq_len: int, rebuild: bool):
        from qdrant_client import QdrantClient
        from qdrant_client.models import Distance, VectorParams
        from sentence_transformers import SentenceTransformer

        from core.config import QDRANT_HOST, QDRANT_PORT

        self.model = SentenceTransformer("BAAI/bge-m3")
        self.model.max_seq_length = max_seq_len
        self.client = QdrantClient(host=QDRANT_HOST, port=QDRANT_PORT)
        self.collection = f"eval_{text}_{max_seq_len}"

        exists = self.client.collection_exists(self.collection)
        if exists and rebuild:
            self.client.delete_collection(self.collection)
            exists = False
        if not exists:
            self.client.create_collection(
                self.collection,
                vectors_config=VectorParams(size=1024, distance=Distance.COSINE),
            )
            self._index(policies, text)
        else:
            print(f"reuse collection {self.collection} (--rebuild 로 재생성)")

    def _index(self, policies: list[Policy], text: str):
        from qdrant_client.models import PointStruct

        targets = [policy for policy in policies if self._document_text(policy, text)]
        print(f"indexing {len(targets)} policies into {self.collection} ...")

        for start in range(0, len(targets), 256):
            chunk = targets[start:start + 256]
            vectors = self.model.encode(
                [self._document_text(policy, text) for policy in chunk],
                batch_size=32,
                normalize_embeddings=True,
            )
            self.client.upsert(
                self.collection,
                points=[
                    PointStruct(id=policy.id, vector=vector.tolist(), payload={"externalId": policy.external_id})
                    for policy, vector in zip(chunk, vectors)
                ],
            )
            print(f"  {min(start + 256, len(targets))}/{len(targets)}")

    @staticmethod
    def _document_text(policy: Policy, text: str) -> str | None:
        if text == "raw":
            return policy.raw_text
        # 운영 임베딩과 같은 텍스트 (제목·설명·기관·분야)
        from domain.embedding.mappers import build_policy_embedding_text
        from domain.embedding.schemas import PolicyEmbeddingRequest

        return build_policy_embedding_text(PolicyEmbeddingRequest(
            policyId=policy.id,
            title=policy.title or "",
            description=policy.description or "",
            organization=policy.organization,
            region=policy.region,
            category=policy.category,
        ))

    def search(self, query: str, limit: int) -> list[str]:
        vector = self.model.encode(query, normalize_embeddings=True).tolist()
        points = self.client.query_points(
            self.collection, query=vector, limit=limit, with_payload=True
        ).points
        return [point.payload["externalId"] for point in points]


def summarize(rows: list[dict]) -> dict[str, dict]:
    groups = defaultdict(list)
    for row in rows:
        groups[row["source"]].append(row["scores"])
        groups["all"].append(row["scores"])
    return {group: {"n": len(scores), **mean_scores(scores)} for group, scores in groups.items()}


def print_summary(summary: dict[str, dict]):
    metric_keys = [key for key in next(iter(summary.values())) if key != "n"]
    print("\n" + " | ".join(["group".ljust(10), "n".rjust(4)] + [key.rjust(9) for key in metric_keys]))
    for group, values in summary.items():
        cells = [group.ljust(10), str(values["n"]).rjust(4)]
        cells += [f"{values[key]:.3f}".rjust(9) for key in metric_keys]
        print(" | ".join(cells))


def print_failures(rows: list[dict], titles: dict[str, str], limit: int):
    failures = [row for row in rows if row["scores"]["hit@5"] == 0.0]
    if not failures or limit <= 0:
        return
    print(f"\n--- Top5 실패 {len(failures)}건 중 {min(limit, len(failures))}건 ---")
    for row in failures[:limit]:
        rank = row["rank"] if row["rank"] else f">{MAX_K}"
        print(f"\n[{row['id']}] {row['query']}")
        print(f"  정답 (순위 {rank}): " + ", ".join(titles.get(i, i) for i in row["relevant"]))
        for position, policy_id in enumerate(row["retrieved"][:3], start=1):
            print(f"  검색 {position}: {titles.get(policy_id, policy_id)}")


def save_results(name: str, config: dict, rows: list[dict], summary: dict[str, dict]):
    RESULT_DIR.mkdir(parents=True, exist_ok=True)
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    detail_path = RESULT_DIR / f"{name}.json"
    detail_path.write_text(
        json.dumps({"config": config, "summary": summary, "rows": rows}, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )

    summary_path = RESULT_DIR / "summary.csv"
    metric_keys = list(summary["all"].keys())
    is_new = not summary_path.exists()
    with summary_path.open("a", encoding="utf-8-sig", newline="") as file:
        writer = csv.writer(file)
        if is_new:
            writer.writerow(["timestamp", "name", "config", "group"] + metric_keys)
        for group, values in summary.items():
            writer.writerow(
                [timestamp, name, json.dumps(config, ensure_ascii=False), group]
                + [round(values[key], 4) if key != "n" else values[key] for key in metric_keys]
            )
    print(f"\nsaved {detail_path} / {summary_path}")


def main():
    parser = argparse.ArgumentParser(description="정책 검색 품질 평가")
    parser.add_argument("--mode", choices=["api", "local"], default="api")
    parser.add_argument("--name", required=True, help="실험 이름 (결과 파일명)")
    parser.add_argument("--dataset", type=Path, nargs="+", default=DEFAULT_DATASETS)
    parser.add_argument("--api-url", default="http://localhost:8000")
    parser.add_argument("--text", choices=["meta", "raw"], default="meta", help="local: 임베딩할 문서 텍스트")
    parser.add_argument("--max-seq-len", type=int, default=128, help="local: bge-m3 max_seq_length")
    parser.add_argument("--rebuild", action="store_true", help="local: eval 컬렉션 재생성")
    parser.add_argument("--with-profile", action="store_true", help="질문에 프로필을 붙여 검색 (서비스 방식)")
    parser.add_argument("--show-failures", type=int, default=10)
    args = parser.parse_args()

    policies = load_policies()
    titles = {policy.external_id: policy.title for policy in policies}
    items = load_dataset(args.dataset, set(titles))
    if not items:
        raise SystemExit("평가할 질문이 없습니다. evaluation/data 를 확인하세요.")

    if args.mode == "api":
        retriever = ApiRetriever(args.api_url, policies)
        config = {"mode": "api", "with_profile": args.with_profile}
    else:
        retriever = LocalRetriever(policies, args.text, args.max_seq_len, args.rebuild)
        config = {"mode": "local", "text": args.text, "max_seq_len": args.max_seq_len,
                  "with_profile": args.with_profile}

    rows = []
    for index, item in enumerate(items, start=1):
        retrieved = retriever.search(build_search_query(item, args.with_profile), MAX_K)
        relevant = set(item["relevant"])
        rows.append({
            "id": item.get("id", str(index)),
            "source": item["source"],
            "query": item["query"],
            "relevant": item["relevant"],
            "retrieved": retrieved,
            "rank": first_relevant_rank(retrieved, relevant),
            "scores": evaluate_query(retrieved, relevant),
        })
        print(f"\r{index}/{len(items)}", end="", flush=True)

    summary = summarize(rows)
    print_summary(summary)
    print_failures(rows, titles, args.show_failures)
    save_results(args.name, config, rows, summary)


if __name__ == "__main__":
    main()
