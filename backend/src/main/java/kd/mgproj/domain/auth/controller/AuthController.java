package kd.mgproj.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import kd.mgproj.core.security.jwt.JwtProvider;
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

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
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
    public ResponseEntity<Void> login(
            @RequestBody LoginRequestDto loginRequestDto
            ) {
        TokenResponseDto token = authService.login(loginRequestDto);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", token.accessToken())
                .httpOnly(true)
                .secure(false) //운영 HTTPS 에선 true
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", token.refreshToken())
                .httpOnly(true)
                .secure(false) //운영 HTTPS 에선 true
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .build();
    }

    @Operation(summary = "로그아웃")
    @PostMapping(value = {"/logout"})
    public ResponseEntity<Void> logout() {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE,accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE,refreshCookie.toString())
                .build();
    }

    /**
     * 토큰 재발급 요청
     * @param request Header에서 Token
     * @return        AccessToken,RefreshToken
     */
    @Operation(summary = "토큰 재발급")
    @PostMapping(value = {"/reissue"})
    public ResponseEntity<Void> reissue(HttpServletRequest request) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);

        TokenResponseDto token = authService.reissue(refreshToken);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", token.accessToken())
                .httpOnly(true)
                .secure(false) //운영 HTTPS 에선 true
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", token.refreshToken())
                .httpOnly(true)
                .secure(false) //운영 HTTPS 에선 true
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .build();
    }
}


