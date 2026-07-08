package kd.mgproj.domain.conversation.entity;

import jakarta.persistence.*;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.common.enums.MessageRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "messages")
public class MessageEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageRole role;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String referencesJson;

    public static MessageEntity create(
            ConversationEntity conversation,
            MessageRole role,
            String content
    ) {
        MessageEntity message = new MessageEntity();
        message.conversation = conversation;
        message.role = role;
        message.content = content;
        return message;
    }

    public static MessageEntity createAssistantMessage(
            ConversationEntity conversation,
            String content,
            String referencesJson
    ) {
        MessageEntity message = new MessageEntity();
        message.conversation = conversation;
        message.role = MessageRole.ASSISTANT;
        message.content = content;
        message.referencesJson = referencesJson;
        return message;
    }
}