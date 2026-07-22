package kd.mgproj.domain.conversation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import kd.mgproj.common.enums.MessageRole;
import kd.mgproj.domain.conversation.entity.MessageEntity;
import kd.mgproj.domain.recommendation.dto.response.AiChatResponse;

public record MessageResponse(
	Long messageId,
	MessageRole role,
	String content,
	List<AiChatResponse.PolicyReferenceDto> references,
	LocalDateTime createdAt
) {
	public static MessageResponse from(MessageEntity message) {
		return new MessageResponse(
			message.getId(),
			message.getRole(),
			message.getContent(),
			List.of(),
			message.getCreatedAt()
		);
	}

	public static MessageResponse from(
		MessageEntity message,
		List<AiChatResponse.PolicyReferenceDto> references
	) {
		return new MessageResponse(
			message.getId(),
			message.getRole(),
			message.getContent(),
			references == null ? List.of() : references,
			message.getCreatedAt()
		);
	}
}