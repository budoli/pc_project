package kd.mgproj.domain.auth.test;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kd.mgproj.domain.user.dto.request.SignupRequestDto;
import lombok.RequiredArgsConstructor;

@RestController
@Profile("local")
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestSignupController {
	private final TestSignupService testSignupService;

	@PostMapping("/signup")
	public ResponseEntity<Void> signup(@RequestBody SignupRequestDto request) {
		testSignupService.signup(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
