package kd.mgproj.domain.auth.service;

public interface EmailService {
	void sendCode(String email);

	void verifyCode(String email, String code);

	boolean isVerified(String email);

	void deleteVerified(String email);
}
