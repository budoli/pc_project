from fastapi import FastAPI
from domain.embedding.router import router as embedding_router

app = FastAPI()

app.include_router(
  embedding_router,
  prefix="/api/v1/embeddings",
  tags=["Embeddings"]
)

@app.get("/")
def health_check():
  return {"status" : "ok"}