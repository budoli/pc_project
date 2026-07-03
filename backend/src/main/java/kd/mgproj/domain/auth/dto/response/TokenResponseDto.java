package kd.mgproj.domain.auth.dto.response;

public record TokenResponseDto(
        String accessToken,
        String refreshToken
) {}


