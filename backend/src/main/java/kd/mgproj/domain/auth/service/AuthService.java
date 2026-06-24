package kd.mgproj.domain.auth.service;

import kd.mgproj.domain.user.dto.request.LoginRequestDto;
import kd.mgproj.domain.user.dto.request.SignupRequestDto;
import kd.mgproj.domain.auth.dto.TokenResponseDto;

public interface AuthService {
    void signup(SignupRequestDto signupRequestDto);
    TokenResponseDto login(LoginRequestDto loginRequestDto);
    void logout();
    TokenResponseDto reissue(String refreshToken);
}
