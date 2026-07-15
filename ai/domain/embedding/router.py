from fastapi import APIRouter
from .schemas import PolicyEmbeddingRequest, PolicyEmbeddingBatchRequest, SearchRequest
from .mappers import build_policy_embedding_text, build_policy_payload, map_qdrant_result_to_policy
from domain.dependencies import get_embedding_service, get_qdrant_service

router = APIRouter()

@router.on_event("startup")
def startup():
  qdrant_service = get_qdrant_service()
  qdrant_service.create_collection_if_not_exists()

@router.post("/embeddings/policies")
def embed_policy(request: PolicyEmbeddingRequest):
  embedding_service = get_embedding_service()
  qdrant_service = get_qdrant_service()

  vector = embedding_service.embed(build_policy_embedding_text(request))
  payload = build_policy_payload(request)

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
  embedding_service = get_embedding_service()
  qdrant_service = get_qdrant_service()
  saved_count = 0

  for policy in request.policies:
    vector = embedding_service.embed(build_policy_embedding_text(policy))
    payload = build_policy_payload(policy)

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
  embedding_service = get_embedding_service()
  qdrant_service = get_qdrant_service()

  vector = embedding_service.embed(request.query)
  results = qdrant_service.search(vector, request.limit)

  return [map_qdrant_result_to_policy(result) for result in results]
