package kd.mgproj.domain.conversation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kd.mgproj.domain.conversation.entity.ConversationEntity;

public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {
	Optional<ConversationEntity> findByIdAndUserId(Long conversationId, Long userId);

	List<ConversationEntity> findByUserIdOrderByUpdatedAtDesc(Long userId);

}
