package kd.mgproj.domain.user.service;

import kd.mgproj.core.error.ErrorCode;
import kd.mgproj.core.error.exception.BusinessException;
import kd.mgproj.domain.user.dto.response.UserResponseDto;
import kd.mgproj.domain.user.entity.UsersEntity;
import kd.mgproj.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
  private final UserRepository userRepository;

  @Override
  public UserResponseDto getCurrentUser(Long userId){
    UsersEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

   return UserResponseDto.from(user);
  }

}
