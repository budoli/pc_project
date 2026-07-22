package kd.mgproj.domain.conversation.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kd.mgproj.domain.conversation.entity.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
	List<MessageEntity> findByConversationIdOrderByIdAsc(Long conversationId);

	List<MessageEntity> findByConversationIdOrderByIdDesc(
		Long conversationId,
		Pageable pageable
	);
}
