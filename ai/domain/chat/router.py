from fastapi import APIRouter
from .schemas import ChatRequest, ChatResponse
from domain.dependencies import get_chat_service

router = APIRouter()

@router.post("/chat", response_model=ChatResponse)
def chat(request: ChatRequest) :
    chat_service = get_chat_service()

    return chat_service.chat(
        query=request.query,
        limit=request.limit,
        profile=request.profile,
        messages=request.messages
    )
