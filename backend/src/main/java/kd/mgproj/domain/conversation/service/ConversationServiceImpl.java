package kd.mgproj.domain.conversation.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kd.mgproj.common.enums.MessageRole;
import kd.mgproj.core.error.ErrorCode;
import kd.mgproj.core.error.exception.BusinessException;
import kd.mgproj.domain.conversation.dto.request.CreateConversationRequest;
import kd.mgproj.domain.conversation.dto.request.SendMessageRequest;
import kd.mgproj.domain.conversation.dto.response.ConversationResponse;
import kd.mgproj.domain.conversation.dto.response.CreateConversationResponse;
import kd.mgproj.domain.conversation.dto.response.MessageResponse;
import kd.mgproj.domain.conversation.dto.response.SendMessageResponse;
import kd.mgproj.domain.conversation.entity.ConversationEntity;
import kd.mgproj.domain.conversation.entity.MessageEntity;
import kd.mgproj.domain.conversation.repository.ConversationRepository;
import kd.mgproj.domain.conversation.repository.MessageRepository;
import kd.mgproj.domain.policy.repository.PolicyRepository;
import kd.mgproj.domain.recommendation.dto.response.AiChatResponse;
import kd.mgproj.domain.recommendation.service.RecommendationService;
import kd.mgproj.domain.user.entity.UsersEntity;
import kd.mgproj.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {
	private final ObjectMapper objectMapper;
	private final UserRepository userRepository;
	private final MessageRepository messageRepository;
	private final ConversationRepository conversationRepository;
	private final PolicyRepository policyRepository;
	private final RecommendationService recommendationService;

	/**
	 * 대화 생성과 첫 메시지 전송을 한 트랜잭션에서 처리한다.
	 * AI 호출이 실패하면 대화까지 롤백돼 메시지 없는 빈 대화가 남지 않는다.
	 */
	@Override
	@Transactional
	public CreateConversationResponse createConversation(Long userId, CreateConversationRequest request) {
		if (request.firstMessage() == null || request.firstMessage().isBlank()) {
			throw new BusinessException(ErrorCode.EMPTY_MESSAGE);
		}

		UsersEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		ConversationEntity conversation = conversationRepository.save(
			ConversationEntity.create(user, request.firstMessage())
		);

		SendMessageResponse firstExchange = appendExchange(userId, conversation, request.firstMessage());

		return CreateConversationResponse.of(conversation, firstExchange);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConversationResponse> getConversations(Long userId) {
		return conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId)
			.stream()
			.map(ConversationResponse::from)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<MessageResponse> getMessages(Long userId, Long conversationId) {
		ConversationEntity conversation = getConversation(conversationId, userId);

		return messageRepository.findByConversationIdOrderByIdAsc(conversation.getId())
			.stream()
			.map(this::toMessageResponse)
			.toList();
	}

	@Override
	@Transactional
	public SendMessageResponse sendMessage(
		Long userId,
		Long conversationId,
		SendMessageRequest request
	) {
		ConversationEntity conversation = getConversation(conversationId, userId);

		return appendExchange(userId, conversation, request.message());
	}

	// 본인 대화만 삭제할 수 있다. 메시지가 대화를 FK로 참조하므로 메시지부터 지운다
	@Override
	@Transactional
	public void deleteConversation(Long userId, Long conversationId) {
		ConversationEntity conversation = getConversation(conversationId, userId);

		messageRepository.deleteAllByConversationId(conversation.getId());
		conversationRepository.delete(conversation);
	}

	// 사용자 메시지를 저장하고 AI 답변을 받아 저장한다. 호출하는 쪽의 트랜잭션 안에서 실행된다
	private SendMessageResponse appendExchange(Long userId, ConversationEntity conversation, String message) {
		MessageEntity userMessage = messageRepository.save(
			MessageEntity.create(
				conversation,
				MessageRole.USER,
				message
			)
		);

		List<MessageEntity> recentMessages = messageRepository
			.findByConversationIdOrderByIdDesc(
				conversation.getId(),
				PageRequest.of(0, 20)
			)
			.stream()
			.sorted(Comparator.comparing(MessageEntity::getId))
			.toList();

		AiChatResponse aiResponse = recommendationService.chatWithHistory(
			userId,
			message,
			recentMessages
		);

		String referencesJson;

		try {
			referencesJson = objectMapper.writeValueAsString(aiResponse.references());
		} catch (JsonProcessingException e) {
			throw new BusinessException(ErrorCode.INTER_SERVER_ERROR);
		}

		MessageEntity assistantMessage = messageRepository.save(
			MessageEntity.createAssistantMessage(
				conversation,
				aiResponse.answer(),
				referencesJson
			)
		);

		conversation.updateLastMessage(aiResponse.answer());
		conversation.updateTitleIfEmpty(message);

		return SendMessageResponse.of(
			userMessage,
			assistantMessage,
			aiResponse.references()
		);
	}

	private MessageResponse toMessageResponse(MessageEntity message) {
		String referencesJson = message.getReferencesJson();

		if (referencesJson == null || referencesJson.isBlank()) {
			return MessageResponse.from(message);
		}

		try {
			List<AiChatResponse.PolicyReferenceDto> references =
				objectMapper.readValue(
					referencesJson,
					new TypeReference<>() {
					}
				);

			List<AiChatResponse.PolicyReferenceDto> validReferences = references.stream()
				.filter(reference -> reference.policyId() != null)
				.map(reference -> policyRepository.findById(reference.policyId())
					.filter(policy -> Objects.equals(policy.getTitle(), reference.title()))
					.map(policy -> new AiChatResponse.PolicyReferenceDto(
						policy.getId(),
						policy.getTitle(),
						reference.score()
					))
					.orElse(null))
				.filter(Objects::nonNull)
				.toList();

			return MessageResponse.from(message, validReferences);
		} catch (JsonProcessingException e) {
			return MessageResponse.from(message);
		}
	}

	private ConversationEntity getConversation(Long conversationId, Long userId) {
		return conversationRepository.findByIdAndUserId(conversationId, userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.CONVERSATION_NOT_FOUND));
	}
}
