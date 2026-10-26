import re

# 정책 데이터(온통청년 zipCd) 앞 두 자리 기준 시도 코드.
# 광주·전남은 전남광주통합특별시(12)로, 강원·전북·제주는 특별자치도 코드(51·52·50)로 들어온다.
REGION_LABELS = {
    "11": "서울특별시",
    "12": "전남광주통합특별시",
    "26": "부산광역시",
    "27": "대구광역시",
    "28": "인천광역시",
    "30": "대전광역시",
    "31": "울산광역시",
    "36": "세종특별자치시",
    "41": "경기도",
    "43": "충청북도",
    "44": "충청남도",
    "47": "경상북도",
    "48": "경상남도",
    "50": "제주특별자치도",
    "51": "강원특별자치도",
    "52": "전북특별자치도",
}

# "경기"는 "경기가 안 좋다"처럼 경제를 뜻하는 경우가 많아 "경기도"만 인식한다
_REGION_ALIASES = {
    "11": ["서울특별시", "서울시", "서울"],
    "12": ["전남광주통합특별시", "광주광역시", "전라남도", "광주", "전남"],
    "26": ["부산광역시", "부산"],
    "27": ["대구광역시", "대구"],
    "28": ["인천광역시", "인천"],
    "30": ["대전광역시", "대전"],
    "31": ["울산광역시", "울산"],
    "36": ["세종특별자치시", "세종시", "세종"],
    "41": ["경기도"],
    "43": ["충청북도", "충북"],
    "44": ["충청남도", "충남"],
    "47": ["경상북도", "경북"],
    "48": ["경상남도", "경남"],
    "50": ["제주특별자치도", "제주도", "제주"],
    "51": ["강원특별자치도", "강원도", "강원"],
    "52": ["전북특별자치도", "전라북도", "전북"],
}

# 긴 이름을 먼저 맞춰 "전남광주통합특별시"가 "전남"·"광주"로 쪼개져 잡히지 않게 한다
_ALIAS_PATTERN = re.compile(
    "|".join(
        re.escape(alias)
        for alias in sorted(
            (alias for aliases in _REGION_ALIASES.values() for alias in aliases),
            key=len,
            reverse=True,
        )
    )
)
_CODE_BY_ALIAS = {alias: code for code, aliases in _REGION_ALIASES.items() for alias in aliases}


def find_region_codes(text: str | None) -> list[str]:
    """질문에 나온 시도 이름을 시도 코드로 바꿔 나온 순서대로 돌려준다."""
    if not text:
        return []

    codes = []
    for match in _ALIAS_PATTERN.finditer(text):
        code = _CODE_BY_ALIAS[match.group()]
        if code not in codes:
            codes.append(code)

    return codes
