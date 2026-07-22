package kd.mgproj.domain.conversation.dto.response;

import java.util.List;

import kd.mgproj.domain.conversation.entity.MessageEntity;
import kd.mgproj.domain.recommendation.dto.response.AiChatResponse;

public record SendMessageResponse(
	MessageResponse userMessage,
	MessageResponse assistantMessage
) {
	public static SendMessageResponse of(
		MessageEntity userMessage,
		MessageEntity assistantMessage
	) {
		return new SendMessageResponse(
			MessageResponse.from(userMessage),
			MessageResponse.from(assistantMessage)
		);
	}

	public static SendMessageResponse of(
		MessageEntity userMessage,
		MessageEntity assistantMessage,
		List<AiChatResponse.PolicyReferenceDto> references
	) {
		return new SendMessageResponse(
			MessageResponse.from(userMessage),
			MessageResponse.from(assistantMessage, references)
		);
	}
}