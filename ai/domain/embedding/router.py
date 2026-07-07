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

@router.post("/embeddings/policies")
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
    "policyId": request.policyId,
    "title": request.title,
    "description": request.description,
    "organization": request.organization,
    "region": request.region,
    "category": request.category
  }

  qdrant_service.upsert_policy(
    point_id=request.policyId,
    vector=vector,
    payload=payload
  )

  return {
    "message" : "embedding saved",
    "policy_id": request.policyId
  }

@router.post("/embeddings/policies/batch")
def embed_policies_batch(request: PolicyEmbeddingBatchRequest) :
  saved_count = 0

  for policy in request.policies:
    text = f"""
    제목: {policy.title}
    설명: {policy.description}
    기관: {policy.organization}
    지역: {policy.region}
    분야: {policy.category}
    """

    vector = embedding_service.embed(text)

    payload = {
      "policyId" : policy.policyId,
      "title" : policy.title,
      "description" : policy.description,
      "organization" : policy.organization,
      "region" : policy.region,
      "category" : policy.category,
    }

    qdrant_service.upsert_policy(
      point_id=policy.policyId,
      vector=vector,
      payload=payload
    )

    saved_count += 1

  return {
    "message": "batch embedding saved",
    "count" : saved_count
  }

@router.post("/search/similar-policies")
def search_policy(request: SearchRequest) :
  vector = embedding_service.embed(request.query)
  results = qdrant_service.search(vector, request.limit)

  return [
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