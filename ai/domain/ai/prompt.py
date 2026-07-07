def build_policy_prompt(query: str, policies: list[dict]) -> str :
  policy_text = ""

  for index, policy in enumerate(policies,start=1):
    policy_text += f"""
    [{index}]
    정책명: {policy.get("title")}
    기관: {policy.get("organization")}
    지역: {policy.get("region")}
    분야: {policy.get("category")}
    설명: {policy.get("description")}
    """

  return f"""
  당신은 대한민국 청년 정책 추천 전문가입니다.

  아래 검색된 정책 정보만 참고해서 답변하세요.
  검색 결과에 없는 내용은 추측하지 마세요.

  사용자 질문:
  {query}

  검색된 정책:
  {policy_text}

  답변 규칙:
  1. 사용자의 상황에 맞는 정책을 먼저 추천하세요.
  2. 각 정책의 추천 이유를 쉽게 설명하세요.
  3. 신청 방법이나 세부 조건이 검색 결과에 없으면 "추가 확인이 필요합니다"라고 말하세요.
  4. 마지막에 참고한 정책명을 목록으로 정리하세요.
  """
  

  