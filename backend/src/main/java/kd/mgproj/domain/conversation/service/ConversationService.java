package kd.mgproj.domain.conversation.service;

import java.util.List;

import kd.mgproj.domain.conversation.dto.request.CreateConversationRequest;
import kd.mgproj.domain.conversation.dto.request.SendMessageRequest;
import kd.mgproj.domain.conversation.dto.response.ConversationResponse;
import kd.mgproj.domain.conversation.dto.response.MessageResponse;
import kd.mgproj.domain.conversation.dto.response.SendMessageResponse;

public interface ConversationService {
	ConversationResponse createConversation(Long userId, CreateConversationRequest request);

	List<ConversationResponse> getConversations(Long userId);

	List<MessageResponse> getMessages(Long userId, Long conversationId);

	SendMessageResponse sendMessage(Long userId, Long conversationId, SendMessageRequest request);
}
