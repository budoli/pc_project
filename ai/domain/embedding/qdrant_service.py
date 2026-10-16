from qdrant_client import QdrantClient
from qdrant_client.models import Distance, VectorParams, PointStruct
from core.config import QDRANT_HOST, QDRANT_PORT

COLLECTION_NAME = "policy_embeddings"

class QdrantService:
  def __init__(self):
    self.client = QdrantClient(
      host= QDRANT_HOST,
      port= QDRANT_PORT
    )

  def create_collection_if_not_exists(self) :
    if not self._collection_exists() :
      self.client.create_collection(
        collection_name=COLLECTION_NAME,
        vectors_config=VectorParams(
          size=1024,
          distance=Distance.COSINE
        )
      )

  def reset_collection(self) :
    if self._collection_exists() :
      self.client.delete_collection(collection_name=COLLECTION_NAME)

    self.create_collection_if_not_exists()

  def _collection_exists(self) -> bool:
    collections = self.client.get_collections().collections
    return any(collection.name == COLLECTION_NAME for collection in collections)

  def upsert_policy(self, point_id: int, vector: list[float], payload: dict) :
    self.client.upsert(
      collection_name=COLLECTION_NAME,
      points=[
        PointStruct(
          id=point_id,
          vector=vector,
          payload=payload
        )
      ]
    )

  def upsert_policies(self, points) :
    self.client.upsert(
      collection_name=COLLECTION_NAME,
      points=points
    )
  
  def search(self, vector: list[float], limit: int=5) :
    result = self.client.query_points(
      collection_name=COLLECTION_NAME,
      query=vector,
      limit=limit,
      with_payload=True
    )

    return result.points
