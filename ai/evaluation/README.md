# 검색 품질 평가

질문 → 정답 정책(plcyNo) 평가셋으로 검색 결과를 채점한다. LLM 답변이 아니라 **검색 단계만** 평가한다.

## 준비

```bash
cd ai
pip install -r evaluation/requirements.txt
```

`ai/.env`에 백엔드와 같은 DB 접속 정보(`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`)와
`QDRANT_HOST`, `OLLAMA_BASE_URL`, `OLLAMA_MODEL`을 넣는다.

## 1. 평가셋 만들기

```bash
python -m evaluation.generate_queries --count 50     # -> evaluation/data/synthetic.jsonl
```

- 생성된 질문은 **반드시 직접 검수**한다. 정책명을 베꼈거나 원문에 없는 조건(나이 등)을 지어낸 질문은 고치거나 지운다.
- `data/manual.example.jsonl`을 참고해 `data/manual.jsonl`에 직접 작성한 질문 20개 이상을 넣는다.
  정답이 여러 개면 `relevant`에 모두 넣는다. ID는 DB id가 아니라 `external_id`(plcyNo)를 쓴다.

## 2. 평가 실행

```bash
# 현재 서비스 기준선 (AI 서버 실행 중이어야 함)
python -m evaluation.evaluate --mode api --name baseline

# 설정을 바꾼 실험 (eval_* 컬렉션을 따로 만들고, 운영 컬렉션은 건드리지 않음)
python -m evaluation.evaluate --mode local --text meta --max-seq-len 128 --name meta-128
python -m evaluation.evaluate --mode local --text meta --max-seq-len 512 --name meta-512
python -m evaluation.evaluate --mode local --text raw  --max-seq-len 512 --name raw-512
python -m evaluation.evaluate --mode local --text raw  --max-seq-len 512 --with-profile --name raw-512-profile
```

결과는 `results/<name>.json`(질문별 상세)에 저장되고, `results/summary.csv`에 한 줄씩 누적된다.

## 지표

| 지표 | 의미 |
|---|---|
| hit@k | 상위 k개 안에 정답이 하나라도 있으면 1 |
| recall@k | 정답들 중 상위 k개 안에 들어온 비율 (정답이 1개면 hit@k와 같음) |
| mrr@10 | 첫 정답 순위의 역수 평균 (1위=1.0, 2위=0.5, 10위 밖=0) |
