package kd.mgproj.domain.conversation.controller;

import kd.mgproj.core.security.UserDetailsImpl;
import kd.mgproj.domain.conversation.dto.request.CreateConversationRequest;
import kd.mgproj.domain.conversation.dto.request.SendMessageRequest;
import kd.mgproj.domain.conversation.dto.response.ConversationResponse;
import kd.mgproj.domain.conversation.dto.response.MessageResponse;
import kd.mgproj.domain.conversation.dto.response.SendMessageResponse;
import kd.mgproj.domain.conversation.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/conversations")
public class ConversationController {
    private final ConversationService conversationService;

    @PostMapping
    public ResponseEntity<ConversationResponse> createConversation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CreateConversationRequest request
    ) {
        return ResponseEntity.ok(
                conversationService.createConversation(userDetails.getUserId(), request)
        );
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getConversations(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(
                conversationService.getConversations(userDetails.getUserId())
        );
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long conversationId
    ) {
        return ResponseEntity.ok(conversationService.getMessages(userDetails.getUserId(), conversationId));
    }

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
