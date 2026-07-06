from qdrant_client import QdrantClient
from qdrant_client.models import Distance, VectorParams, PointStruct
from database import QDRANT_HOST, QDRANT_PORT

COLLECTION_NAME = "policy_embeddings"

class QdrantService:
  def __init__(self):
    self.client = QdrantClient(
      host= QDRANT_HOST,
      port= QDRANT_PORT
    )

  def create_collection_if_not_exists(self) :
    collections = self.client.get_collections().collections
    names = [collection.name for collection in collections]

    if COLLECTION_NAME not in names :
      self.client.create_collection(
        collection_name=COLLECTION_NAME,
        vectors_config=VectorParams(
          size=1024,
          distance=Distance.COSINE
        )
      )

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
