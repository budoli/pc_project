"""검색 평가 지표.

retrieved: 검색 결과 정책 ID 목록 (순위순)
relevant:  정답 정책 ID 집합
"""

K_VALUES = (1, 3, 5, 10)


def first_relevant_rank(retrieved: list[str], relevant: set[str]) -> int | None:
    for rank, policy_id in enumerate(retrieved, start=1):
        if policy_id in relevant:
            return rank
    return None


def hit_at_k(retrieved: list[str], relevant: set[str], k: int) -> float:
    return 1.0 if any(policy_id in relevant for policy_id in retrieved[:k]) else 0.0


def recall_at_k(retrieved: list[str], relevant: set[str], k: int) -> float:
    if not relevant:
        return 0.0
    return len(relevant.intersection(retrieved[:k])) / len(relevant)


def reciprocal_rank(retrieved: list[str], relevant: set[str], k: int) -> float:
    rank = first_relevant_rank(retrieved[:k], relevant)
    return 1.0 / rank if rank else 0.0


def evaluate_query(retrieved: list[str], relevant: set[str], k_values=K_VALUES) -> dict:
    max_k = max(k_values)
    scores = {f"hit@{k}": hit_at_k(retrieved, relevant, k) for k in k_values}
    scores.update({f"recall@{k}": recall_at_k(retrieved, relevant, k) for k in k_values})
    scores[f"mrr@{max_k}"] = reciprocal_rank(retrieved, relevant, max_k)
    return scores


def mean_scores(rows: list[dict]) -> dict:
    if not rows:
        return {}
    return {key: sum(row[key] for row in rows) / len(rows) for key in rows[0]}
