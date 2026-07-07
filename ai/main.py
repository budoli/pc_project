from fastapi import FastAPI
from domain.embedding.router import router as embedding_router
from domain.chat.router import router as chat_router
app = FastAPI()

app.include_router(
  embedding_router,
  prefix="/api/v1/ai",
  tags=["AI Embeddings"]
)

app.include_router(
  chat_router,
  prefix="/api/v1/ai",
  tags=["AI Chat"]
)

@app.get("/")
def health_check():
  return {"status" : "ok"}