package kd.mgproj.core.security;

import org.springframework.stereotype.Component;

@Component
public class SecurityWhiteList {
	public static final String[] AUTH_WHITELIST = {
		"/v3/api-docs/**",
		"/swagger-ui.html",
		"/swagger-ui/**",

		"/api/v1/auth/signup",
		"/api/v1/auth/login",
		"/api/v1/auth/reissue",
		"/api/v1/auth/email/send",
		"/api/v1/auth/email/verify"
	};
}
