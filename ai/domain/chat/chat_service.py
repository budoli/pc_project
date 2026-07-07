from domain.embedding.embedding_service import EmbeddingService
from domain.embedding.qdrant_service import QdrantService
from domain.ai.gemini_service import GeminiService
from domain.ai.prompt import build_policy_prompt

class ChatService:
  def __init__(self):
    self.embedding_service = EmbeddingService()
    self.qdrant_service = QdrantService()
    self.gemini_service = GeminiService()
  
  def chat(self, query: str, limit: int=5) -> dict:
    vector = self.embedding_service.embed(query)
    results = self.qdrant_service.search(vector, limit) 
    policies = [
      {
          "score": result.score,
          "policyId": result.payload.get("policyId"),
          "title": result.payload.get("title"),
          "description": result.payload.get("description"),
          "organization": result.payload.get("organization"),
          "region": result.payload.get("region"),
          "category": result.payload.get("category"),
      }
      for result in results
    ]

    prompt = build_policy_prompt(query, policies)
    answer = self.gemini_service.generate(prompt)

    references = [
      {
        "policyId": policy.get("policyId"),
        "title": policy.get("title"),
        "score": policy.get("score")
      }
      for policy in policies
    ]

    return {
      "answer": answer,
      "references": references
    }