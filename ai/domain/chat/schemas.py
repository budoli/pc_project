from datetime import date
from pydantic import BaseModel, Field


class UserProfile(BaseModel):
    age: int | None = None
    gender: str | None = None
    region: str | None = None
    regionCode: str | None = None
    jobStatus: str | None = None
    incomeLevel: str | None = None
    householdType: str | None = None
    interests: str | None = None

class ChatMessage(BaseModel):
    role: str
    content: str


class ChatRequest(BaseModel):
    query: str
    limit: int = 5
    profile: UserProfile | None = None
    messages: list[ChatMessage] = Field(default_factory=list)


class PolicyReference(BaseModel):
    policyId: int | None = None
    title: str | None = None
    score: float | None = None


class ChatResponse(BaseModel):
    answer: str
    references: list[PolicyReference]
