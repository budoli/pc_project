package kd.mgproj.domain.auth.service;

import kd.mgproj.common.enums.Role;
import kd.mgproj.core.error.ErrorCode;
import kd.mgproj.core.error.exception.BusinessException;
import kd.mgproj.core.security.jwt.JwtProvider;
import kd.mgproj.domain.user.dto.request.LoginRequestDto;
import kd.mgproj.domain.user.dto.request.SignupRequestDto;
import kd.mgproj.domain.auth.dto.TokenResponseDto;
import kd.mgproj.domain.user.entity.UsersEntity;
import kd.mgproj.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public void signup(SignupRequestDto dto) {
        String email = dto.email();

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        String encodedPassword = passwordEncoder.encode(dto.password());

        UsersEntity user = UsersEntity.createUser(
                dto.name(),
                dto.email(),
                encodedPassword
        );

        userRepository.save(user);
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
    public void logout() {

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
