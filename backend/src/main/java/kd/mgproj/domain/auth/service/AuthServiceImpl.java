package kd.mgproj.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kd.mgproj.common.enums.RegionCode;
import kd.mgproj.common.enums.Role;
import kd.mgproj.core.error.ErrorCode;
import kd.mgproj.core.error.exception.BusinessException;
import kd.mgproj.core.security.jwt.JwtProvider;
import kd.mgproj.domain.auth.dto.response.TokenResponseDto;
import kd.mgproj.domain.profile.entity.UsersProfileEntity;
import kd.mgproj.domain.profile.repository.UserProfileRepository;
import kd.mgproj.domain.user.dto.request.LoginRequestDto;
import kd.mgproj.domain.user.dto.request.SignupRequestDto;
import kd.mgproj.domain.user.entity.UsersEntity;
import kd.mgproj.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final UserRepository userRepository;
	private final UserProfileRepository profileRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;

	@Override
	public void signup(SignupRequestDto dto) {
		String email = dto.email();

		if (!emailService.isVerified(email)) {
			throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
		}

		if (userRepository.existsByEmail(email)) {
			throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
		}

		String encodedPassword = passwordEncoder.encode(dto.password());

		UsersEntity user = UsersEntity.createUser(
			dto.name(),
			dto.email(),
			encodedPassword
		);

		UsersProfileEntity profile = UsersProfileEntity.createUser(
			user,
			dto.birthDate(),
			dto.gender(),
			RegionCode.codeOf(dto.region()),
			dto.jobStatus(),
			dto.incomeLevel(),
			dto.householdType(),
			dto.interests()
		);

		userRepository.save(user);
		profileRepository.save(profile);
		emailService.deleteVerified(email);
	}

	@Override
	public TokenResponseDto login(LoginRequestDto loginRequestDto) {
		String email = loginRequestDto.email();
		String password = loginRequestDto.password();

		UsersEntity user = userRepository.findByEmail(email)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BusinessException(ErrorCode.INVALID_PASSWORD);
		}

		long id = user.getId();
		Role role = user.getRole();

		String access = jwtProvider.generateAccessToken(id, role);
		String refresh = jwtProvider.generateRefreshToken(id, role);

		return new TokenResponseDto(access, refresh);
	}

	@Override
	public TokenResponseDto reissue(String refreshToken) {

		if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
			throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		Long userId = jwtProvider.getUserId(refreshToken);

		UsersEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		String newAccessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
		String newRefreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getRole());

		return new TokenResponseDto(
			newAccessToken,
			newRefreshToken
		);
	}
}
