package kd.mgproj.domain.auth.service;

import kd.mgproj.domain.auth.dto.response.TokenResponseDto;
import kd.mgproj.domain.user.dto.request.LoginRequestDto;
import kd.mgproj.domain.user.dto.request.SignupRequestDto;

public interface AuthService {
	void signup(SignupRequestDto signupRequestDto);

	TokenResponseDto login(LoginRequestDto loginRequestDto);

	TokenResponseDto reissue(String refreshToken);
}
