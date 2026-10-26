"""정책 원문(rawText)으로 합성 평가 질문을 만든다.

정책 하나를 골라 LLM에게 "이 정책이 필요한 청년이 할 법한 질문"을 만들게 하고,
그 정책을 정답으로 기록한다.

실행 (ai 디렉터리에서):
    python -m evaluation.generate_queries --count 50
"""
import argparse
import json
import random
import re
from pathlib import Path

import httpx

from core.config import OLLAMA_BASE_URL, OLLAMA_MODEL
from evaluation.db import Policy, load_policies

DEFAULT_OUT = Path(__file__).parent / "data" / "synthetic.jsonl"
MAX_SOURCE_CHARS = 1500

PROMPT_TEMPLATE = """아래는 청년 정책 원문입니다.

{raw_text}

이 정책이 꼭 필요한 청년이 정책 상담 챗봇에 처음 보낼 법한 질문을 한 문장으로 만드세요.
규칙:
- 정책명, 원문 문장, 숫자 조건, 행정 용어(중위소득, 무주택세대구성원 등)를 그대로 쓰지 마세요.
- 자기 상황(나이, 사는 지역, 처지)과 필요한 도움을 평범한 구어체로 말하세요.
- 이 정책을 다른 정책과 구분할 수 있는 단서(지역, 대상, 지원 형태)를 하나 이상 넣으세요.
- 질문 한 문장만 출력하세요."""


def build_prompt(policy: Policy) -> str:
    return PROMPT_TEMPLATE.format(raw_text=policy.raw_text[:MAX_SOURCE_CHARS])


def clean_question(text: str, policy_title: str) -> str | None:
    text = re.sub(r"<think>.*?</think>", "", text, flags=re.DOTALL)
    lines = [line.strip() for line in text.splitlines() if line.strip()]
    if not lines:
        return None

    question = re.sub(r"^(질문\s*[:：]|\d+[.)])\s*", "", lines[0]).strip("\"'“”‘’ ")

    if not 10 <= len(question) <= 200:
        return None
    # 정책명을 그대로 쓰면 키워드 일치만으로 맞히는 쉬운 문제가 된다
    if policy_title and policy_title.strip() in question:
        return None
    return question


def generate(prompt: str, model: str) -> str:
    response = httpx.post(
        f"{OLLAMA_BASE_URL}/api/generate",
        json={
            "model": model,
            "prompt": prompt,
            "stream": False,
            "think": False,
            "options": {"temperature": 0.7},
        },
        timeout=300.0,
    )
    response.raise_for_status()
    return response.json()["response"]


def main():
    parser = argparse.ArgumentParser(description="합성 평가 질문 생성")
    parser.add_argument("--count", type=int, default=50)
    parser.add_argument("--seed", type=int, default=42)
    parser.add_argument("--model", default=OLLAMA_MODEL)
    parser.add_argument("--out", type=Path, default=DEFAULT_OUT)
    args = parser.parse_args()

    policies = [policy for policy in load_policies() if policy.raw_text]
    random.Random(args.seed).shuffle(policies)

    args.out.parent.mkdir(parents=True, exist_ok=True)
    written = 0

    with args.out.open("w", encoding="utf-8") as out:
        for policy in policies:
            if written >= args.count:
                break

            question = clean_question(generate(build_prompt(policy), args.model), policy.title)
            if question is None:
                print(f"skip  {policy.external_id} {policy.title}")
                continue

            written += 1
            item = {
                "id": f"syn-{written:03d}",
                "query": question,
                "relevant": [policy.external_id],
                "source": "synthetic",
                "note": policy.title,
            }
            out.write(json.dumps(item, ensure_ascii=False) + "\n")
            print(f"[{written}/{args.count}] {question}")

    print(f"saved {written} questions -> {args.out}")


if __name__ == "__main__":
    main()
