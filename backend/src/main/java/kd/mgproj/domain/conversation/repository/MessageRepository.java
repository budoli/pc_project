package kd.mgproj.domain.conversation.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kd.mgproj.domain.conversation.entity.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
	List<MessageEntity> findByConversationIdOrderByIdAsc(Long conversationId);

	List<MessageEntity> findByConversationIdOrderByIdDesc(
		Long conversationId,
		Pageable pageable
	);

	@Modifying
	@Query("delete from MessageEntity m where m.conversation.id = :conversationId")
	void deleteAllByConversationId(@Param("conversationId") Long conversationId);
}
