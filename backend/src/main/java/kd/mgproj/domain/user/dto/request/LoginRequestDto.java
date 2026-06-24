package kd.mgproj.domain.user.dto.request;

public record LoginRequestDto(
   String email,
   String password
) {}
