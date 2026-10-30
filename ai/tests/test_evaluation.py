import json
import tempfile
import unittest
from pathlib import Path

from evaluation.evaluate import load_dataset, summarize
from evaluation.generate_queries import clean_question
from evaluation.metrics import evaluate_query, hit_at_k, mean_scores, recall_at_k, reciprocal_rank


class MetricsTest(unittest.TestCase):
    def test_single_answer_at_rank_three(self):
        retrieved = ["a", "b", "c", "d"]
        relevant = {"c"}

        self.assertEqual(hit_at_k(retrieved, relevant, 1), 0.0)
        self.assertEqual(hit_at_k(retrieved, relevant, 3), 1.0)
        self.assertAlmostEqual(reciprocal_rank(retrieved, relevant, 10), 1 / 3)

    def test_recall_counts_each_answer(self):
        retrieved = ["a", "x", "b", "y", "z"]
        relevant = {"a", "b", "c", "d"}

        self.assertEqual(recall_at_k(retrieved, relevant, 1), 0.25)
        self.assertEqual(recall_at_k(retrieved, relevant, 5), 0.5)

    def test_answer_outside_k_scores_zero(self):
        retrieved = [str(i) for i in range(10)] + ["answer"]

        scores = evaluate_query(retrieved, {"answer"})

        self.assertEqual(scores["hit@10"], 0.0)
        self.assertEqual(scores["mrr@10"], 0.0)

    def test_mean_scores(self):
        rows = [{"hit@1": 1.0}, {"hit@1": 0.0}]

        self.assertEqual(mean_scores(rows), {"hit@1": 0.5})


class DatasetTest(unittest.TestCase):
    def test_skips_items_without_known_answer(self):
        lines = [
            {"query": "q1", "relevant": ["P1"]},
            {"query": "q2", "relevant": ["<정답 정책 plcyNo>"]},
            {"query": "q3", "relevant": ["P2", "STALE"]},
        ]
        with tempfile.TemporaryDirectory() as tmp:
            path = Path(tmp) / "manual.jsonl"
            path.write_text("\n".join(json.dumps(line, ensure_ascii=False) for line in lines), encoding="utf-8")

            items = load_dataset([path], known_ids={"P1", "P2"})

        self.assertEqual([item["query"] for item in items], ["q1", "q3"])
        self.assertEqual(items[1]["relevant"], ["P2"])
        self.assertEqual(items[0]["source"], "manual")

    def test_summarize_groups_by_source(self):
        rows = [
            {"source": "synthetic", "scores": {"hit@5": 1.0}},
            {"source": "manual", "scores": {"hit@5": 0.0}},
        ]

        summary = summarize(rows)

        self.assertEqual(summary["all"], {"n": 2, "hit@5": 0.5})
        self.assertEqual(summary["manual"]["n"], 1)


class CleanQuestionTest(unittest.TestCase):
    def test_strips_prefix_quotes_and_think(self):
        raw = "<think>생각</think>\n질문: \"서울 사는데 월세 지원 받을 수 있을까요?\"\n부연 설명"

        self.assertEqual(clean_question(raw, "청년 월세 지원"), "서울 사는데 월세 지원 받을 수 있을까요?")

    def test_rejects_title_leak_and_bad_length(self):
        self.assertIsNone(clean_question("청년도약계좌 가입하고 싶은데 어떻게 해요?", "청년도약계좌"))
        self.assertIsNone(clean_question("월세?", "청년 월세 지원"))


if __name__ == "__main__":
    unittest.main()
