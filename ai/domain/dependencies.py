from functools import lru_cache

from domain.ai.ollama_service import OllamaService
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
def get_ollama_service() -> OllamaService:
    return OllamaService()


@lru_cache
def get_chat_service() -> ChatService:
    return ChatService(
        embedding_service=get_embedding_service(),
        qdrant_service=get_qdrant_service(),
        ai_service=get_ollama_service(),
    )
