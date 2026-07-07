from google import genai
from database import GEMINI_API_KEY, GEMINI_MODEL

class GeminiService:
  def __init__(self):
    self.client = genai.Client(api_key=GEMINI_API_KEY)

  def generate(self, prompt: str) -> str :

    print("GEMINI_MODEL =", GEMINI_MODEL)
    print("PROMPT VALUE =", repr(prompt))

    if prompt is None or not str(prompt).strip():
      raise ValueError("Gemini prompt is empty")


    response = self.client.models.generate_content(
      model=GEMINI_MODEL,
      contents=prompt
    )

    return response.text