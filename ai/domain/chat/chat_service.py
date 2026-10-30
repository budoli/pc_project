from domain.ai.prompt import build_policy_prompt
from domain.chat.region import REGION_LABELS, find_region_codes
from domain.embedding.mappers import map_qdrant_result_to_policy


class ChatService:
    def __init__(self, embedding_service, qdrant_service, ai_service):
        self.embedding_service = embedding_service
        self.qdrant_service = qdrant_service
        self.ai_service = ai_service

    def chat(self, query: str, limit: int = 5, profile=None, messages=None) -> dict:
        # 질문에 시도 이름이 있으면 프로필 지역보다 우선한다 ("부산 정책 알려줘" 등)
        query_region_codes = find_region_codes(query)
        if query_region_codes:
            profile = self._with_region(profile, query_region_codes)
        region_codes = query_region_codes or self._profile_region_codes(profile)

        search_query = self._build_search_query(query, profile)
        vector = self.embedding_service.embed(search_query)

        results = self.qdrant_service.search(vector, max(limit * 20, 100))
        results = self._filter_by_region_codes(results, region_codes)[:limit]

        policies = [map_qdrant_result_to_policy(result) for result in results]

        prompt = build_policy_prompt(query, policies, profile=profile, messages=messages or [])
        answer = self.ai_service.generate(prompt)

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

    def _with_region(self, profile, region_codes: list[str]):
        # 검색 질의와 답변 프롬프트에도 질문의 지역이 들어가도록 프로필 지역을 바꾼다
        if profile is None:
            return None

        return profile.model_copy(update={
            "region": ", ".join(REGION_LABELS[code] for code in region_codes),
            "regionCode": region_codes[0],
        })

    def _profile_region_codes(self, profile) -> list[str]:
        region_code = getattr(profile, "regionCode", None) if profile else None
        if not region_code:
            return []

        region_code = str(region_code).strip()[:2]
        return [region_code] if region_code else []

    def _filter_by_region_codes(self, results, region_codes: list[str]):
        if not region_codes:
            return results

        return [
            result
            for result in results
            if self._matches_region_codes(result.payload.get("region"), region_codes)
        ]


    def _matches_region_codes(self, policy_region, region_codes: list[str]) -> bool:
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
            code.startswith("00") or any(code.startswith(region_code) for region_code in region_codes)
            for code in codes
        )
