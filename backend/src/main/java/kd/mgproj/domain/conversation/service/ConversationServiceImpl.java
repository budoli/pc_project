package kd.mgproj.domain.conversation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kd.mgproj.common.enums.MessageRole;
import kd.mgproj.core.error.ErrorCode;
import kd.mgproj.core.error.exception.BusinessException;
import kd.mgproj.domain.conversation.dto.request.CreateConversationRequest;
import kd.mgproj.domain.conversation.dto.request.SendMessageRequest;
import kd.mgproj.domain.conversation.dto.response.ConversationResponse;
import kd.mgproj.domain.conversation.dto.response.MessageResponse;
import kd.mgproj.domain.conversation.dto.response.SendMessageResponse;
import kd.mgproj.domain.conversation.entity.ConversationEntity;
import kd.mgproj.domain.conversation.entity.MessageEntity;
import kd.mgproj.domain.conversation.repository.ConversationRepository;
import kd.mgproj.domain.conversation.repository.MessageRepository;
import kd.mgproj.domain.recommendation.dto.response.AiChatResponse;
import kd.mgproj.domain.recommendation.service.RecommendationService;
import kd.mgproj.domain.user.entity.UsersEntity;
import kd.mgproj.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService{
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final RecommendationService recommendationService;

    @Override
    @Transactional
    public ConversationResponse createConversation(Long userId, CreateConversationRequest request) {
        UsersEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ConversationEntity conversation = ConversationEntity.create(
                user,
                request.firstMessage()
        );

        ConversationEntity saveConversation =
                conversationRepository.save(conversation);


        return ConversationResponse.from(saveConversation);
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

        MessageEntity userMessage = messageRepository.save(
                MessageEntity.create(
                        conversation,
                        MessageRole.USER,
                        request.message()
                )
        );

        List<MessageEntity> recentMessages = messageRepository
                .findByConversationIdOrderByIdDesc(
                        conversationId,
                        PageRequest.of(0,20)
                )
                .stream()
                .sorted(Comparator.comparing(MessageEntity::getId))
                .toList();

        AiChatResponse aiResponse = recommendationService.chatWithHistory(
                userId,
                request.message(),
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
        conversation.updateTitleIfEmpty(request.message());

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

            return MessageResponse.from(message, references);
        }catch (JsonProcessingException e) {
            return MessageResponse.from(message);
        }
    }

    private ConversationEntity getConversation(Long conversationId, Long userId) {
        return conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONVERSATION_NOT_FOUND));
    }
}
