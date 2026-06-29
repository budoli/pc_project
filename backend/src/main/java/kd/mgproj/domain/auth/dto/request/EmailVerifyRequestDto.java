package kd.mgproj.domain.auth.dto.request;

public record EmailVerifyRequestDto(
        String email,
        String code
) {}


