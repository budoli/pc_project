package kd.mgproj.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kd.mgproj.domain.auth.dto.request.EmailSendRequestDto;
import kd.mgproj.domain.auth.dto.request.EmailVerifyRequestDto;
import kd.mgproj.domain.auth.service.EmailService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth/email")
public class EmailController {

	private final EmailService emailService;

	@PostMapping("/send")
	public ResponseEntity<Void> sendCode(
		@RequestBody EmailSendRequestDto sendDto
	) {

		emailService.sendCode(sendDto.email());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/verify")
	public ResponseEntity<Void> verifyCode(
		@RequestBody EmailVerifyRequestDto verifyDto
	) {
		emailService.verifyCode(verifyDto.email(), verifyDto.code());
		return ResponseEntity.ok().build();
	}
}
