import type { SelectOption } from "@/shared/constants/regions";

export const INTEREST_OPTIONS = ["취업", "주거", "창업", "교육", "복지", "금융"];

export const GENDER_OPTIONS: SelectOption[] = [
  { label: "선택", value: "" },
  { label: "남성", value: "M" },
  { label: "여성", value: "F" },
];

export const JOB_STATUS_OPTIONS: SelectOption[] = [
  { label: "선택", value: "" },
  { label: "학생", value: "STUDENT" },
  { label: "취업준비생", value: "JOB_SEEKER" },
  { label: "직장인", value: "EMPLOYED" },
  { label: "자영업자", value: "SELF_EMPLOYED" },
  { label: "프리랜서", value: "FREELANCER" },
  { label: "미취업", value: "UNEMPLOYED" },
  { label: "퇴직", value: "RETIRED" },
  { label: "기타", value: "ETC" },
];

export const INCOME_LEVEL_OPTIONS: SelectOption[] = [
  { label: "선택", value: "" },
  { label: "저소득", value: "LOW" },
  { label: "중간", value: "MIDDLE" },
  { label: "고소득", value: "HIGH" },
  { label: "모름", value: "UNKNOWN" },
];

export const HOUSEHOLD_TYPE_OPTIONS: SelectOption[] = [
  { label: "선택", value: "" },
  { label: "1인가구", value: "SINGLE" },
  { label: "부부", value: "COUPLE" },
  { label: "자녀 있는 가구", value: "FAMILY_WITH_CHILDREN" },
  { label: "한부모 가구", value: "SINGLE_PARENT" },
  { label: "다인 가구", value: "MULTI_CHILD" },
  { label: "기타", value: "ETC" },
];
