package kd.mgproj.domain.conversation.dto.response;

import kd.mgproj.domain.conversation.entity.ConversationEntity;

public record CreateConversationResponse(
	ConversationResponse conversation,
	MessageResponse userMessage,
	MessageResponse assistantMessage
) {
	public static CreateConversationResponse of(
		ConversationEntity conversation,
		SendMessageResponse firstExchange
	) {
		return new CreateConversationResponse(
			ConversationResponse.from(conversation),
			firstExchange.userMessage(),
			firstExchange.assistantMessage()
		);
	}
}
