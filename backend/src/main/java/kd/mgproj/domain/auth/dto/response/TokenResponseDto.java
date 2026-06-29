package kd.mgproj.domain.auth.dto;

public record TokenResponseDto(
        String accessToken,
        String refreshToken
) {}


