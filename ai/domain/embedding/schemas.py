from pydantic import BaseModel
from typing import Optional

class PolicyEmbeddingRequest(BaseModel) :
  policy_id:int
  title: str
  description: str
  organization: Optional[str] = None
  region: Optional[str] = None
  category: Optional[str] = None

class PolicyEmbeddingBatchRequest(BaseModel) :
  policies: list[PolicyEmbeddingRequest]

class SearchRequest(BaseModel):
  query: str
  limit: int = 5


