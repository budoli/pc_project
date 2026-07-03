package kd.mgproj.domain.user.service;

import kd.mgproj.domain.user.dto.response.UserResponseDto;

public interface UserService {
  UserResponseDto getCurrentUser(Long userId);
}
