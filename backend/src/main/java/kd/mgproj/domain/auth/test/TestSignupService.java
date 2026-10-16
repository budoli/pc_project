package kd.mgproj.domain.auth.test;

import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kd.mgproj.common.enums.RegionCode;
import kd.mgproj.core.error.ErrorCode;
import kd.mgproj.core.error.exception.BusinessException;
import kd.mgproj.domain.profile.entity.UsersProfileEntity;
import kd.mgproj.domain.profile.repository.UserProfileRepository;
import kd.mgproj.domain.user.dto.request.SignupRequestDto;
import kd.mgproj.domain.user.entity.UsersEntity;
import kd.mgproj.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@Profile("local")
@RequiredArgsConstructor
public class TestSignupService {
	private final UserRepository userRepository;
	private final UserProfileRepository profileRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void signup(SignupRequestDto request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
		}

		UsersEntity user = userRepository.save(UsersEntity.createUser(
			request.name(), request.email(), passwordEncoder.encode(request.password())
		));
		profileRepository.save(UsersProfileEntity.createUser(
			user,
			request.birthDate(),
			request.gender(),
			RegionCode.codeOf(request.region()),
			request.jobStatus(),
			request.incomeLevel(),
			request.householdType(),
			request.interests()
		));
	}
}
