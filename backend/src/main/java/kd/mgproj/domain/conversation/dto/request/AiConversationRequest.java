package kd.mgproj.domain.conversation.dto.request;

import java.util.List;

public record AiConversationRequest(
        String query,
        List<AiMessage> messages
) {
    public record AiMessage(
            String role,
            String content
    ) {
    }
}