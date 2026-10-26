package kd.mgproj.domain.conversation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import kd.mgproj.core.security.UserDetailsImpl;
import kd.mgproj.domain.conversation.dto.request.CreateConversationRequest;
import kd.mgproj.domain.conversation.dto.request.SendMessageRequest;
import kd.mgproj.domain.conversation.dto.response.ConversationResponse;
import kd.mgproj.domain.conversation.dto.response.CreateConversationResponse;
import kd.mgproj.domain.conversation.dto.response.MessageResponse;
import kd.mgproj.domain.conversation.dto.response.SendMessageResponse;
import kd.mgproj.domain.conversation.service.ConversationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/conversations")
public class ConversationController {
	private final ConversationService conversationService;

	@Operation(summary = "채팅 API/대화 생성 및 첫 메시지 전송")
	@PostMapping
	public ResponseEntity<CreateConversationResponse> createConversation(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody CreateConversationRequest request
	) {
		return ResponseEntity.ok(
			conversationService.createConversation(userDetails.getUserId(), request)
		);
	}

	@Operation(summary = "채팅 API/대화 목록 조회")
	@GetMapping
	public ResponseEntity<List<ConversationResponse>> getConversations(
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		return ResponseEntity.ok(
			conversationService.getConversations(userDetails.getUserId())
		);
	}

	@Operation(summary = "채팅 API/대화 메시지 목록 조회")
	@GetMapping("/{conversationId}/messages")
	public ResponseEntity<List<MessageResponse>> getMessages(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long conversationId
	) {
		return ResponseEntity.ok(conversationService.getMessages(userDetails.getUserId(), conversationId));
	}

	@Operation(summary = "채팅 API/대화 삭제")
	@DeleteMapping("/{conversationId}")
	public ResponseEntity<Void> deleteConversation(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long conversationId
	) {
		conversationService.deleteConversation(userDetails.getUserId(), conversationId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "채팅 API/메시지 전송")
	@PostMapping("/{conversationId}/messages")
	public ResponseEntity<SendMessageResponse> sendMessage(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long conversationId,
		@RequestBody SendMessageRequest request
	) {
		return ResponseEntity.ok(
			conversationService.sendMessage(
				userDetails.getUserId(),
				conversationId,
				request
			)
		);
	}
}
