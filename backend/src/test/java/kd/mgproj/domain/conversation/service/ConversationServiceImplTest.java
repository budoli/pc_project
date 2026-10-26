package kd.mgproj.domain.conversation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClientException;

import kd.mgproj.core.error.ErrorCode;
import kd.mgproj.core.error.exception.BusinessException;
import kd.mgproj.domain.conversation.dto.request.CreateConversationRequest;
import kd.mgproj.domain.conversation.dto.response.CreateConversationResponse;
import kd.mgproj.domain.conversation.entity.ConversationEntity;
import kd.mgproj.domain.conversation.repository.ConversationRepository;
import kd.mgproj.domain.conversation.repository.MessageRepository;
import kd.mgproj.domain.recommendation.dto.response.AiChatResponse;
import kd.mgproj.domain.recommendation.service.RecommendationService;
import kd.mgproj.domain.user.entity.UsersEntity;
import kd.mgproj.domain.user.repository.UserRepository;

// 트랜잭션 롤백을 실제 DB로 확인해야 해서 테스트 메서드에는 @Transactional을 붙이지 않는다
@SpringBootTest
class ConversationServiceImplTest {
	private static final String QUESTION = "서울 사는 취준생이 받을 수 있는 월세 지원 있어?";

	@Autowired
	private ConversationService conversationService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ConversationRepository conversationRepository;
	@Autowired
	private MessageRepository messageRepository;

	@MockitoBean
	private RecommendationService recommendationService;

	private Long userId;

	@BeforeEach
	void setUp() {
		UsersEntity user = userRepository.save(UsersEntity.createUser(
			"대화테스트", "conversation-test-" + UUID.randomUUID() + "@local.test", "unused"
		));
		userId = user.getId();
	}

	@AfterEach
	void tearDown() {
		List<ConversationEntity> conversations = conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId);
		conversations.forEach(conversation -> messageRepository.deleteAll(
			messageRepository.findByConversationIdOrderByIdAsc(conversation.getId())
		));
		conversationRepository.deleteAll(conversations);
		userRepository.deleteById(userId);
	}

	@Test
	void createsConversationWithFirstExchange() {
		when(recommendationService.chatWithHistory(any(), anyString(), anyList()))
			.thenReturn(new AiChatResponse("월세 지원 정책을 안내합니다.", List.of()));

		CreateConversationResponse response =
			conversationService.createConversation(userId, new CreateConversationRequest(QUESTION));

		Long conversationId = response.conversation().conversationId();
		assertEquals(QUESTION, response.userMessage().content());
		assertEquals("월세 지원 정책을 안내합니다.", response.assistantMessage().content());
		assertEquals(2, messageRepository.findByConversationIdOrderByIdAsc(conversationId).size());
	}

	@Test
	void leavesNoConversationWhenAiFails() {
		when(recommendationService.chatWithHistory(any(), anyString(), anyList()))
			.thenThrow(new RestClientException("AI server down"));

		assertThrows(RestClientException.class, () ->
			conversationService.createConversation(userId, new CreateConversationRequest(QUESTION))
		);

		assertTrue(conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId).isEmpty());
	}

	@Test
	void deletesOwnConversationWithMessages() {
		Long conversationId = createConversationWithAnswer(userId);

		conversationService.deleteConversation(userId, conversationId);

		assertTrue(conversationRepository.findById(conversationId).isEmpty());
		assertTrue(messageRepository.findByConversationIdOrderByIdAsc(conversationId).isEmpty());
	}

	@Test
	void cannotDeleteOtherUsersConversation() {
		Long conversationId = createConversationWithAnswer(userId);
		Long otherUserId = userRepository.save(UsersEntity.createUser(
			"다른사용자", "conversation-test-" + UUID.randomUUID() + "@local.test", "unused"
		)).getId();

		try {
			BusinessException exception = assertThrows(BusinessException.class, () ->
				conversationService.deleteConversation(otherUserId, conversationId)
			);

			assertEquals(ErrorCode.CONVERSATION_NOT_FOUND, exception.getErrorCode());
			assertEquals(2, messageRepository.findByConversationIdOrderByIdAsc(conversationId).size());
		} finally {
			userRepository.deleteById(otherUserId);
		}
	}

	@Test
	void rejectsBlankFirstMessage() {
		assertThrows(BusinessException.class, () ->
			conversationService.createConversation(userId, new CreateConversationRequest(" "))
		);

		assertTrue(conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId).isEmpty());
	}

	private Long createConversationWithAnswer(Long ownerId) {
		when(recommendationService.chatWithHistory(any(), anyString(), anyList()))
			.thenReturn(new AiChatResponse("월세 지원 정책을 안내합니다.", List.of()));

		return conversationService.createConversation(ownerId, new CreateConversationRequest(QUESTION))
			.conversation()
			.conversationId();
	}
}
