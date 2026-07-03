package kd.mgproj.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import kd.mgproj.core.security.jwt.JwtProvider;
import kd.mgproj.domain.auth.dto.response.LoginResponseDto;
import kd.mgproj.domain.user.dto.request.LoginRequestDto;
import kd.mgproj.domain.user.dto.request.SignupRequestDto;
import kd.mgproj.domain.auth.dto.response.TokenResponseDto;
import kd.mgproj.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto
            ) {
        TokenResponseDto token = authService.login(loginRequestDto);

        ResponseCookie cookie = buildRefreshCookie(token.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponseDto(token.accessToken()));

    }

    @Operation(summary = "로그아웃")
    @PostMapping(value = {"/logout"})
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE,deleteCookie.toString())
                .build();
    }

    /**
     * 토큰 재발급 요청
     * @param request Header에서 Token
     * @return        RefreshToken
     */
    @Operation(summary = "토큰 재발급")
    @PostMapping(value = {"/reissue"})
    public ResponseEntity<LoginResponseDto> reissue(HttpServletRequest request) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);

        TokenResponseDto token = authService.reissue(refreshToken);

        ResponseCookie cookie = buildRefreshCookie(token.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponseDto(token.accessToken()));

    }

    private ResponseCookie buildRefreshCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60*60*24*7)
                .build();
    }
}


