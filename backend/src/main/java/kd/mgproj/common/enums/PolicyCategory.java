package kd.mgproj.common.enums;

public enum PolicyCategory {
  JOB,
  HOUSING,
  EDUCATION,
  WELFARE,
  PARTICIPATION,
  ETC;

  public static PolicyCategory from(String value) {
    if (value == null || value.isBlank()) {
      return ETC;
    }

    return switch (value.trim()) {
      case "일자리" -> JOB;
      case "주거" -> HOUSING;
      case "교육" -> EDUCATION;
      case "복지문화" -> WELFARE;
      case "참여권리" -> PARTICIPATION;
      default -> ETC;
    };
  }
}