package kd.mgproj.domain.conversation.dto.response;

import kd.mgproj.domain.conversation.entity.MessageEntity;

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
}