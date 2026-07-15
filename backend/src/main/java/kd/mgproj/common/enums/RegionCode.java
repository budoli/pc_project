package kd.mgproj.common.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RegionCode {
    SEOUL("11", "서울특별시"),
    GWANGJU_JEONNAM_INTEGRATED_SPECIAL("12", "전남광주통합특별시"),
    BUSAN_DIRECT("21", "부산직할시"),
    DAEGU_DIRECT("22", "대구직할시"),
    INCHEON_DIRECT("23", "인천직할시"),
    GWANGJU_DIRECT("24", "광주직할시"),
    DAEJEON_DIRECT("25", "대전직할시"),
    BUSAN("26", "부산광역시"),
    DAEGU("27", "대구광역시"),
    INCHEON("28", "인천광역시"),
    GWANGJU("29", "광주광역시"),
    DAEJEON("30", "대전광역시"),
    ULSAN("31", "울산광역시"),
    SEJONG("36", "세종특별자치시"),
    GYEONGGI("41", "경기도"),
    GANGWON("42", "강원도"),
    CHUNGBUK("43", "충청북도"),
    CHUNGNAM("44", "충청남도"),
    JEONBUK("45", "전라북도"),
    JEONNAM("46", "전라남도"),
    GYEONGBUK("47", "경상북도"),
    GYEONGNAM("48", "경상남도"),
    JEJU("49", "제주도"),
    JEJU_SPECIAL("50", "제주특별자치도"),
    GANGWON_SPECIAL("51", "강원특별자치도"),
    JEONBUK_SPECIAL("52", "전북특별자치도");

    private final String code;
    private final String label;

    RegionCode(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static String codeOf(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalizedValue = value.trim();
        String aliasCode = codeOfAlias(normalizedValue);
        if (aliasCode != null) {
            return aliasCode;
        }

        return Arrays.stream(values())
                .filter(region -> isSameCode(region.code, normalizedValue)
                        || region.label.equals(normalizedValue)
                        || region.label.contains(normalizedValue)
                        || normalizedValue.contains(region.label))
                .map(RegionCode::getCode)
                .findFirst()
                .orElse(normalizedValue);
    }

    private static boolean isSameCode(String code, String value) {
        return code.equals(value) || value.matches("\\d+") && value.startsWith(code);
    }

    private static String codeOfAlias(String value) {
        return switch (value) {
            case "서울", "서울시", "서울특별시" -> SEOUL.code;
            case "부산", "부산시", "부산광역시" -> BUSAN.code;
            case "대구", "대구시", "대구광역시" -> DAEGU.code;
            case "인천", "인천시", "인천광역시" -> INCHEON.code;
            case "광주", "광주시", "광주광역시" -> GWANGJU.code;
            case "대전", "대전시", "대전광역시" -> DAEJEON.code;
            case "울산", "울산시", "울산광역시" -> ULSAN.code;
            case "세종", "세종시", "세종특별자치시" -> SEJONG.code;
            case "경기", "경기도" -> GYEONGGI.code;
            case "강원", "강원도", "강원특별자치도" -> GANGWON_SPECIAL.code;
            case "충북", "충청북도" -> CHUNGBUK.code;
            case "충남", "충청남도" -> CHUNGNAM.code;
            case "전북", "전라북도", "전북특별자치도" -> JEONBUK_SPECIAL.code;
            case "전남", "전라남도" -> JEONNAM.code;
            case "경북", "경상북도" -> GYEONGBUK.code;
            case "경남", "경상남도" -> GYEONGNAM.code;
            case "제주", "제주도", "제주특별자치도" -> JEJU_SPECIAL.code;
            default -> null;
        };
    }

    public static String labelOf(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }

        String normalizedCode = code.trim();

        return Arrays.stream(values())
                .filter(region -> region.code.equals(normalizedCode))
                .map(RegionCode::getLabel)
                .findFirst()
                .orElse(normalizedCode);
    }
}
