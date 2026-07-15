from domain.ai.gemini_service import GeminiService
from domain.ai.prompt import build_policy_prompt
from domain.embedding.embedding_service import EmbeddingService
from domain.embedding.qdrant_service import QdrantService


class ChatService:
    def __init__(self):
        self.embedding_service = EmbeddingService()
        self.qdrant_service = QdrantService()
        self.gemini_service = GeminiService()

    def chat(self, query: str, limit: int = 5, profile=None, messages=None) -> dict:
        search_query = self._build_search_query(query, profile)
        vector = self.embedding_service.embed(search_query)

        results = self.qdrant_service.search(vector, max(limit * 20, 100))
        results = self._filter_by_region_code(results, profile)[:limit]

        policies = [
            {
                "score": result.score,
                "policyId": result.payload.get("policyId"),
                "title": result.payload.get("title"),
                "description": result.payload.get("description"),
                "organization": result.payload.get("organization"),
                "region": result.payload.get("region"),
                "category": result.payload.get("category"),
            }
            for result in results
        ]

        prompt = build_policy_prompt(query, policies, profile=profile, messages=messages or [])
        answer = self.gemini_service.generate(prompt)

        references = [
            {
                "policyId": policy.get("policyId"),
                "title": policy.get("title"),
                "score": policy.get("score"),
            }
            for policy in policies
        ]

        return {
            "answer": answer,
            "references": references,
        }

    def _build_search_query(self, query: str, profile) -> str:
        if profile is None:
            return query

        profile_parts = [
            f"나이: {profile.age}" if profile.age is not None else None,
            f"성별: {profile.gender}" if profile.gender else None,
            f"지역: {profile.region}" if profile.region else None,
            f"지역 코드: {profile.regionCode}" if getattr(profile, "regionCode", None) else None,
            f"직업 상태: {profile.jobStatus}" if profile.jobStatus else None,
            f"소득 수준: {profile.incomeLevel}" if profile.incomeLevel else None,
            f"가구 유형: {profile.householdType}" if profile.householdType else None,
            f"관심 분야: {profile.interests}" if profile.interests else None,
        ]

        profile_text = "\n".join(part for part in profile_parts if part)

        return f"""
        사용자 질문:
        {query}

        사용자 프로필:
        {profile_text}
        """

    def _filter_by_region_code(self, results, profile):
        region_code = getattr(profile, "regionCode", None) if profile else None
        if not region_code:
            return results

        region_code = str(region_code).strip()[:2]
        if not region_code:
            return results

        return [
            result
            for result in results
            if self._matches_region_code(result.payload.get("region"), region_code)
        ]


    def _matches_region_code(self, policy_region, region_code: str) -> bool:
        if policy_region is None:
            return False

        codes = [
            code.strip()
            for code in str(policy_region)
            .replace("|", ",")
            .replace(" ", ",")
            .split(",")
            if code.strip()
        ]

        return any(
            code.startswith(region_code) or code.startswith("00")
			for code in codes)
