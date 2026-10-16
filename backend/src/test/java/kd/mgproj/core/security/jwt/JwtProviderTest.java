package kd.mgproj.core.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.Cookie;
import kd.mgproj.common.enums.Role;
import kd.mgproj.core.security.UserDetailServiceImpl;

class JwtProviderTest {
	private JwtProvider jwtProvider;

	@BeforeEach
	void setUp() {
		jwtProvider = new JwtProvider(mock(UserDetailServiceImpl.class));
		ReflectionTestUtils.setField(jwtProvider, "secretKey", "test-secret-key-with-at-least-32-bytes");
		ReflectionTestUtils.setField(jwtProvider, "accessExpiration", 60_000L);
		ReflectionTestUtils.setField(jwtProvider, "refreshExpiration", 120_000L);
	}

	@Test
	void accessAndRefreshTokensAreNotInterchangeable() {
		String access = jwtProvider.generateAccessToken(1L, Role.USER);
		String refresh = jwtProvider.generateRefreshToken(1L, Role.USER);

		assertTrue(jwtProvider.validateAccessToken(access));
		assertFalse(jwtProvider.validateRefreshToken(access));
		assertTrue(jwtProvider.validateRefreshToken(refresh));
		assertFalse(jwtProvider.validateAccessToken(refresh));
	}

	@Test
	void resolvesEachTokenFromItsOwnCookie() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setCookies(new Cookie("accessToken", "access"), new Cookie("refreshToken", "refresh"));

		assertEquals("access", jwtProvider.resolveAccessToken(request));
		assertEquals("refresh", jwtProvider.resolveRefreshToken(request));
	}
}
