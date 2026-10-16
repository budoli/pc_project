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
            },
            timeout=300.0,
        )
        response.raise_for_status()

        return response.json()["response"]