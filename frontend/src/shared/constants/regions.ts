export type SelectOption = {
  label: string;
  value: string;
};

export const REGION_LABELS: Record<string, string> = {
  "00": "전국",
  "11": "서울특별시",
  "12": "전남광주통합특별시",
  "21": "부산직할시",
  "22": "대구직할시",
  "23": "인천직할시",
  "24": "광주직할시",
  "25": "대전직할시",
  "26": "부산광역시",
  "27": "대구광역시",
  "28": "인천광역시",
  "29": "광주광역시",
  "30": "대전광역시",
  "31": "울산광역시",
  "36": "세종특별자치시",
  "41": "경기도",
  "42": "강원도",
  "43": "충청북도",
  "44": "충청남도",
  "45": "전라북도",
  "46": "전라남도",
  "47": "경상북도",
  "48": "경상남도",
  "49": "제주도",
  "50": "제주특별자치도",
  "51": "강원특별자치도",
  "52": "전북특별자치도",
};

export const REGION_OPTIONS: SelectOption[] = [
  { label: "선택", value: "" },
  { label: "서울특별시", value: "11" },
  { label: "부산광역시", value: "26" },
  { label: "대구광역시", value: "27" },
  { label: "인천광역시", value: "28" },
  { label: "광주광역시", value: "29" },
  { label: "대전광역시", value: "30" },
  { label: "울산광역시", value: "31" },
  { label: "세종특별자치시", value: "36" },
  { label: "경기도", value: "41" },
  { label: "강원특별자치도", value: "51" },
  { label: "충청북도", value: "43" },
  { label: "충청남도", value: "44" },
  { label: "전북특별자치도", value: "52" },
  { label: "전라남도", value: "46" },
  { label: "경상북도", value: "47" },
  { label: "경상남도", value: "48" },
  { label: "제주특별자치도", value: "50" },
];

export const formatRegion = (region?: string | null) => {
  if (!region) {
    return "-";
  }

  const labels = region
    .replaceAll("|", ",")
    .split(/[,\s]+/)
    .map((code) => code.trim().slice(0, 2))
    .filter(Boolean)
    .map((code) => REGION_LABELS[code] ?? code);

  return [...new Set(labels)].join(", ") || region;
};
