from fastapi import APIRouter
from qdrant_client.models import PointStruct
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

  if not request.policies:
    return {
      "message": "batch embedding saved",
      "count": 0
    }

  vectors = embedding_service.embed_batch([
    build_policy_embedding_text(policy)
    for policy in request.policies
  ])

  points = [
    PointStruct(
      id=policy.policyId,
      vector=vector,
      payload=build_policy_payload(policy)
    )
    for policy, vector in zip(request.policies, vectors)
  ]

  qdrant_service.upsert_policies(points)

  return {
    "message": "batch embedding saved",
    "count" : len(points)
  }

@router.post("/search/similar-policies")
def search_policy(request: SearchRequest) :
  embedding_service = get_embedding_service()
  qdrant_service = get_qdrant_service()

  vector = embedding_service.embed(request.query)
  results = qdrant_service.search(vector, request.limit)

  return [map_qdrant_result_to_policy(result) for result in results]
