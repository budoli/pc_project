package kd.mgproj.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import kd.mgproj.core.security.jwt.JwtProvider;
import kd.mgproj.domain.user.dto.request.LoginRequestDto;
import kd.mgproj.domain.user.dto.request.SignupRequestDto;
import kd.mgproj.domain.auth.dto.TokenResponseDto;
import kd.mgproj.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor

public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    /**
     * 회원가입 요청
     * @param signupRequestDto 로그인을 하기 위한 최소한의 정보
     * @return                 201 created
     */
    @Operation(summary = "회원가입")
    @PostMapping(value = {"/signup"})
    public ResponseEntity<Void> signup(
            @RequestBody SignupRequestDto signupRequestDto
            ) {
        authService.signup(signupRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 로그인 요청
     * @param loginRequestDto email, password
     * @return                AccessToken, RefreshToken
     */
    @Operation(summary = "로그인")
    @PostMapping(value = {"/login"})
    public ResponseEntity<TokenResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto
            ) {
        authService.login(loginRequestDto);

        return ResponseEntity.ok(authService.login(loginRequestDto));

    }

    @Operation(summary = "로그아웃")
    @PostMapping(value = {"/logout"})
    public ResponseEntity<Void> logout() {
        authService.logout();

        return ResponseEntity.noContent().build();
    }

    /**
     * 토큰 재발급 요청
     * @param request Header에서 Token
     * @return        RefreshToken
     */
    @Operation(summary = "토큰 재발급")
    @PostMapping(value = {"/reissue"})
    public ResponseEntity<TokenResponseDto> reissue(HttpServletRequest request) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);

        return ResponseEntity.ok(authService.reissue(refreshToken));

    }
}


