import httpx

from core.config import OLLAMA_BASE_URL, OLLAMA_MODEL


class OllamaService:
    def generate(self, prompt: str) -> str:
        response = httpx.post(
            f"{OLLAMA_BASE_URL}/api/generate",
            json={
                "model": OLLAMA_MODEL,
                "prompt": prompt,
                "stream": False,
                # gemma4·qwen3.5 등은 기본으로 thinking을 해 응답이 느려지고, 긴 프롬프트에서는 response가 비기도 해 끈다
                "think": False,
            },
            timeout=300.0,
        )
        response.raise_for_status()

        return response.json()["response"]