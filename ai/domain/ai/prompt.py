def build_policy_prompt(
    query: str,
    policies: list[dict],
    profile: None,
    messages: list | None=None
) -> str :
    profile_text = build_profile_text(profile)
    history_text = build_history_text(messages or [])
    policy_text = build_policy_text(policies)

    return f"""
    당신은 대한민국 청년 정책 추천 전문가입니다.

    아래 검색된 정책 정보만 참고해서 답변하세요.
    검색 결과에 없는 내용은 추측하지 마세요.


    답변 규칙:
    1. 사용자의 상황에 맞는 정책을 먼저 추천하세요.
    2. 사용자의 지역에 맞는 정책을 먼저 추천하세요.
    3. 각 정책의 추천 이유를 쉽게 설명하세요.
    4. 신청 방법이나 세부 조건이 검색 결과에 없으면 "추가 확인이 필요합니다"라고 말하세요.
    5. 마지막에 참고한 정책명을 목록으로 정리하세요.
    6. 답변은 마크다운으로 작성하세요. 정책을 비교할 때는 표를 써도 됩니다.
    7. LaTeX 수식($...$)은 쓰지 말고, 화살표 등은 → 같은 문자를 그대로 쓰세요.
    8. 유사도 점수나 [1] 같은 검색 결과 번호는 답변에 쓰지 마세요.

    사용자 프로필:
    {profile_text}

    최근 대화 맥락:
    {history_text}
    
    사용자 질문:
    {query}

    검색된 정책:
    {policy_text}
    
    """
    

def build_profile_text(profile) -> str:
    if profile is None:
        return "프로필 정보 없음"
    
    rows = [
        ("나이", profile.age),
        ("성별", profile.gender),
        ("지역", profile.region),
        ("직업 상태", profile.jobStatus),
        ("소득 수준", profile.incomeLevel),
        ("가구 유형", profile.householdType),
        ("관심 분야", profile.interests),
    ]

    text = "\n".join(
        f"- {label}: {value}"
        for label, value in rows
        if value is not None and value != ""
    )

    return text if text else "프로필 정보 없음"

def build_history_text(messages:list) -> str:
    if not messages:
        return "최근 대화 없음"
    
    return "\n".join(
        f"- {message.role}: {message.content}"
        for message in messages[-6:]
    )

def build_policy_text(policies: list[dict]) -> str:
    if not policies:
        return "검색된 정책 없음"

    policy_text = ""

    for index, policy in enumerate(policies, start=1):
        policy_text += f"""
        [{index}]
            정책명: {policy.get("title")}
            기관: {policy.get("organization")}
            지역: {policy.get("region")}
            분야: {policy.get("category")}
            유사도 점수: {policy.get("score")}
            설명: {policy.get("description")}
        """

    return policy_text