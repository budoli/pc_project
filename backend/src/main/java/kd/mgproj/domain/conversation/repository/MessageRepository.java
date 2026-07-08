package kd.mgproj.domain.conversation.repository;

import kd.mgproj.domain.conversation.entity.MessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByConversationIdOrderByIdAsc(Long conversationId);

    List<MessageEntity> findByConversationIdOrderByIdDesc(
            Long conversationId,
            Pageable pageable
    );
}
