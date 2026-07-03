package kd.mgproj.domain.user.controller;

import kd.mgproj.core.security.UserDetailsImpl;
import kd.mgproj.domain.user.dto.response.UserResponseDto;
import kd.mgproj.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> getCurrentUser(
          @AuthenticationPrincipal UserDetailsImpl userDetails
          ) {
    Long userId = userDetails.getUserId();

    return ResponseEntity.ok(userService.getCurrentUser(userId));
  }
}
