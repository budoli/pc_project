from sentence_transformers import SentenceTransformer

class EmbeddingService:
  def __init__(self):
    self.model = SentenceTransformer("BAAI/bge-m3")
    self.model.max_seq_length = 128
  
  def embed(self, text: str) -> list[float]:
    vector = self.model.encode(
      text,
      normalize_embeddings=True
    )
    return vector.tolist()

  def embed_batch(self, texts: list[str]) -> list[list[float]]:
    vectors = self.model.encode(
      texts,
      batch_size=32,
      normalize_embeddings=True
    )
    return vectors.tolist()
