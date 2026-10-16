import unittest
from unittest.mock import Mock, patch

from domain.embedding.router import embed_policies_batch
from domain.embedding.schemas import PolicyEmbeddingBatchRequest, PolicyEmbeddingRequest


class EmbeddingBatchTest(unittest.TestCase):
    def setUp(self):
        self.request = PolicyEmbeddingBatchRequest(
            policies=[PolicyEmbeddingRequest(policyId=1, title="Policy", description="Description")]
        )
        self.embedding_service = Mock()
        self.qdrant_service = Mock()
        self.embedding_patch = patch(
            "domain.embedding.router.get_embedding_service",
            return_value=self.embedding_service,
        )
        self.qdrant_patch = patch(
            "domain.embedding.router.get_qdrant_service",
            return_value=self.qdrant_service,
        )
        self.embedding_patch.start()
        self.qdrant_patch.start()
        self.addCleanup(self.embedding_patch.stop)
        self.addCleanup(self.qdrant_patch.stop)

    def test_embedding_failure_preserves_existing_collection(self):
        self.embedding_service.embed_batch.side_effect = RuntimeError("model unavailable")

        with self.assertRaises(RuntimeError):
            embed_policies_batch(self.request)

        self.qdrant_service.reset_collection.assert_not_called()
        self.qdrant_service.upsert_policies.assert_not_called()

    def test_success_upserts_without_resetting_collection(self):
        self.embedding_service.embed_batch.return_value = [[0.0] * 1024]

        result = embed_policies_batch(self.request)

        self.assertEqual(1, result["count"])
        self.qdrant_service.reset_collection.assert_not_called()
        self.qdrant_service.upsert_policies.assert_called_once()


if __name__ == "__main__":
    unittest.main()
