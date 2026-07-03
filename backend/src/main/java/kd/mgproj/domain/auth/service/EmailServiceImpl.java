package kd.mgproj.domain.auth.service;

import kd.mgproj.core.error.ErrorCode;
import kd.mgproj.core.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

  private static final String CODE_PREFIX = "email:code:";
  private static final String VERIFIED_PREFIX = "email:verified:";

  private final StringRedisTemplate redisTemplate;
  private final JavaMailSender mailSender;

  @Override
  public void sendCode(String email) {
    String code = createCode();

    redisTemplate.opsForValue().set(
            CODE_PREFIX + email,
            code,
            Duration.ofMinutes(3)
    );

    sendMail(email, code);
  }

  @Override
  public void verifyCode(String email, String code) {

    String savedCode = redisTemplate.opsForValue().get(CODE_PREFIX + email);

    if (savedCode == null) {
      throw new BusinessException(ErrorCode.EMAIL_CODE_EXPIRED);
    }

    if (!savedCode.equals(code)) {
      throw new BusinessException(ErrorCode.EMAIL_CODE_MISMATCH);
    }

    redisTemplate.delete(CODE_PREFIX + email);

    redisTemplate.opsForValue().set(
            VERIFIED_PREFIX + email,
            "true",
            Duration.ofMinutes(30)
    );

  }

  @Override
  public boolean isVerified(String email) {

    return "true".equals(redisTemplate.opsForValue().get(VERIFIED_PREFIX + email));
  }

  @Override
  public void deleteVerified(String email) {
    redisTemplate.delete(VERIFIED_PREFIX + email);
  }

  private String createCode() {
    return String.valueOf((int) (Math.random() * 900000)+ 100000);
  }

  private void sendMail(String to, String code) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject("[Policy Compass] 인증번호");
    message.setText("인증번호는" + code + "입니다.");

    mailSender.send(message);
  }
}
