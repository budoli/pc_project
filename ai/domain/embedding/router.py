from fastapi import APIRouter
from .schemas import PolicyEmbeddingRequest, PolicyEmbeddingBatchRequest, SearchRequest
from .embedding_service import EmbeddingService
from .qdrant_service import QdrantService

router = APIRouter()

embedding_service = EmbeddingService()
qdrant_service = QdrantService()

@router.on_event("startup")
def startup():
  qdrant_service.create_collection_if_not_exists()

@router.post("/policies")
def embed_policy(request: PolicyEmbeddingRequest):
  text = f"""
  제목: {request.title}
  설명: {request.description}
  기관: {request.organization}
  지역: {request.region}
  분야: {request.category}
  """

  vector = embedding_service.embed(text)

  payload = {
    "policy_id": request.policy_id,
    "title": request.title,
    "description": request.description,
    "organization": request.organization,
    "region": request.region,
    "category": request.category
  }

  qdrant_service.upsert_policy(
    point_id=request.policy_id,
    vector=vector,
    payload=payload
  )

  return {
    "message" : "embedding saved",
    "policy_id": request.policy_id
  }

@router.post("/policies/batch")
def embed_policies_batch(request: PolicyEmbeddingBatchRequest) :
  ...

@router.post("/search")
def search_policy(request: SearchRequest) :
  vector = embedding_service.embed(request.query)
  results = qdrant_service.search(vector, request.limit)

  return [
    {
      "score": result.score,
      "policyId": result.payload.get("policy_id"),
      "title": result.payload.get("title"),
      "description": result.payload.get("description"),
      "organization": result.payload.get("organization"),
      "region": result.payload.get("region"),
      "category": result.payload.get("category"),
    }
    for result in results
  ]