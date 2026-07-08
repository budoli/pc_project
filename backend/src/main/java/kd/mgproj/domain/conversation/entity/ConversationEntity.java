package kd.mgproj.domain.conversation.entity;

import jakarta.persistence.*;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.domain.user.entity.UsersEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "conversations")
public class ConversationEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String lastMessage;

    public static ConversationEntity create(UsersEntity user, String firstMessage) {
        ConversationEntity conversation = new ConversationEntity();
        conversation.user = user;
        conversation.title = makeTitle(firstMessage);
        conversation.lastMessage = firstMessage;
        return conversation;
    }

    public void updateLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void updateTitleIfEmpty(String message) {
        if (this.title == null || this.title.isBlank() || this.title.equals("새 대화")) {
            this.title = makeTitle(message);
        }
    }

    private static String makeTitle(String message) {
        if (message == null || message.isBlank()) {
            return "새 대화";
        }

        return message.length() > 30
                ? message.substring(0, 30)
                : message;
    }
}