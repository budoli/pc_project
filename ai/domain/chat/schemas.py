from pydantic import BaseModel

class ChatRequest(BaseModel):
  query: str
  limit: int=5

class PolicyReference(BaseModel):
  policyId: int | None = None
  title: str | None = None
  score: float | None = None

class ChatResponse(BaseModel):
  answer: str
  references: list[PolicyReference]


