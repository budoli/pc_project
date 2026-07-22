package kd.mgproj.core.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kd.mgproj.common.enums.Role;
import kd.mgproj.core.security.UserDetailServiceImpl;
import kd.mgproj.core.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 생성, 검증, 토큰 정보 추출을 담당하는 Provider
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
	private static final String ACCESS_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	private static final String CLAIM_ROLE = "role";
	private final UserDetailServiceImpl detailsService;
	@Value("${jwt.secret}")
	private String secretKey;
	@Value("${jwt.access-expiration}")
	private long accessExpiration;
	@Value("${jwt.refresh-expiration}")
	private long refreshExpiration;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(
			secretKey.getBytes(StandardCharsets.UTF_8)
		);
	}

	/**
	 *  Access Token 생성
	 * @param userId 사용자 id
	 * @return       JWT Access Token
	 */
	public String generateAccessToken(Long userId, Role role) {
		return Jwts.builder()
			.subject(userId.toString())
			.claim(CLAIM_ROLE, role.name())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + accessExpiration))
			.signWith(getSigningKey())
			.compact();
	}

	/**
	 * Refresh Token 생성
	 * @param userId 사용자 id
	 * @return       JWT Refresh Token
	 */
	public String generateRefreshToken(Long userId, Role role) {
		return Jwts.builder()
			.subject(userId.toString())
			.claim(CLAIM_ROLE, role.name())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + refreshExpiration))
			.signWith(getSigningKey())
			.compact();
	}

	public String resolveAccessToken(HttpServletRequest req) {
		String bearerToken = resolveToken(req.getHeader(ACCESS_HEADER));

		if (bearerToken != null) {
			return bearerToken;
		}

		Cookie[] cookies = req.getCookies();

		if (cookies == null) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if ("refreshToken".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}

		return null;
	}

	public String resolveRefreshToken(HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();

		if (cookies == null) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if ("accessToken".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}

		return null;
	}

	public String resolveToken(String header) {
		if (header != null && header.startsWith(BEARER_PREFIX)) {
			return header.substring(BEARER_PREFIX.length());
		}
		return null;
	}

	/**
	 * JWT 토근 유효성 검증
	 * @param token JWT Token
	 * @return      유효하면 true, 아니면 false
	 */
	public boolean validateToken(String token) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();

			return claims.getExpiration().after(new Date());
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * JWT에서 사용자 ID 추출
	 * @param token JWT Token
	 * @return      사용자 EMAIL
	 */
	public Long getUserId(String token) {
		String subject = Jwts.parser()
			.verifyWith(getSigningKey())
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getSubject();

		return Long.valueOf(subject);
	}

	public Role getRole(String token) {
		String role = Jwts.parser()
			.verifyWith(getSigningKey())
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get(CLAIM_ROLE, String.class);

		return Role.valueOf(role);
	}

	public Authentication getAuthentication(String token) {
		Long userId = getUserId(token);

		UserDetailsImpl userDetails = detailsService.loadUserById(userId);

		return new UsernamePasswordAuthenticationToken(
			userDetails,
			null,
			userDetails.getAuthorities()
		);
	}
}


