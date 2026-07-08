package kd.mgproj.domain.conversation.dto.response;

import kd.mgproj.domain.conversation.entity.ConversationEntity;

import java.time.LocalDateTime;

public record ConversationResponse(
        Long conversationId,
        String title,
        String lastMessage,
        LocalDateTime updatedAt
) {
    public static ConversationResponse from(ConversationEntity conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getLastMessage(),
                conversation.getUpdatedAt()
        );
    }
}