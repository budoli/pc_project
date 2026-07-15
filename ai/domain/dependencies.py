from functools import lru_cache

from domain.ai.gemini_service import GeminiService
from domain.chat.chat_service import ChatService
from domain.embedding.embedding_service import EmbeddingService
from domain.embedding.qdrant_service import QdrantService


@lru_cache
def get_embedding_service() -> EmbeddingService:
    return EmbeddingService()


@lru_cache
def get_qdrant_service() -> QdrantService:
    return QdrantService()


@lru_cache
def get_gemini_service() -> GeminiService:
    return GeminiService()


@lru_cache
def get_chat_service() -> ChatService:
    return ChatService(
        embedding_service=get_embedding_service(),
        qdrant_service=get_qdrant_service(),
        gemini_service=get_gemini_service(),
    )
