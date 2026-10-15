package kd.mgproj.domain.recommendation.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import kd.mgproj.core.error.ErrorCode;
import kd.mgproj.core.error.exception.BusinessException;
import kd.mgproj.domain.conversation.dto.request.AiConversationRequest;
import kd.mgproj.domain.conversation.entity.MessageEntity;
import kd.mgproj.domain.policy.repository.PolicyRepository;
import kd.mgproj.domain.profile.entity.UsersProfileEntity;
import kd.mgproj.domain.profile.repository.UserProfileRepository;
import kd.mgproj.domain.recommendation.dto.request.RecommendationRequest;
import kd.mgproj.domain.recommendation.dto.response.AiChatResponse;
import kd.mgproj.domain.recommendation.dto.response.RecommendationDetailResponse;
import kd.mgproj.domain.recommendation.dto.response.RecommendationHistoryResponse;
import kd.mgproj.domain.recommendation.dto.response.RecommendationResponse;
import kd.mgproj.domain.recommendation.entity.RecommendationEntity;
import kd.mgproj.domain.recommendation.entity.RecommendationPolicyEntity;
import kd.mgproj.domain.recommendation.repository.RecommendationPolicyRepository;
import kd.mgproj.domain.recommendation.repository.RecommendationRepository;
import kd.mgproj.domain.user.entity.UsersEntity;
import kd.mgproj.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
	private final WebClient webClient;
	private final UserRepository userRepository;
	private final UserProfileRepository profileRepository;
	private final PolicyRepository policyRepository;
	private final RecommendationRepository recommendationRepository;
	private final RecommendationPolicyRepository recommendationPolicyRepository;

	@Value("${ai.server.url}")
	private String aiServerUrl;

	@Override
	public RecommendationResponse recommend(Long userId, RecommendationRequest request) {
		UsersEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		UsersProfileEntity profile = profileRepository.findByUsersId(userId)
			.orElse(null);

		AiConversationRequest aiRequest = new AiConversationRequest(
			request.query(),
			AiConversationRequest.UserProfilePayload.from(profile),
			List.of()
		);

		AiChatResponse aiResponse = webClient.post()
			.uri(aiServerUrl + "/api/v1/ai/chat")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(aiRequest)
			.retrieve()
			.bodyToMono(AiChatResponse.class)
			.block();

		if (aiResponse == null) {
			throw new BusinessException(ErrorCode.AI_RESPONSE_FAILED);
		}

		List<AiChatResponse.PolicyReferenceDto> validReferences =
			filterExistingPolicyReferences(aiResponse.references());

		RecommendationEntity recommendation = RecommendationEntity.create(
			user,
			request.query(),
			aiResponse.answer()
		);

		RecommendationEntity savedRecommend =
			recommendationRepository.save(recommendation);

		List<RecommendationPolicyEntity> recommendationPolicies =
			validReferences.stream()
				.filter(reference -> reference.policyId() != null)
				.map(reference -> policyRepository.findById(reference.policyId())
					.map(policy -> RecommendationPolicyEntity.create(
						savedRecommend,
						policy,
						reference.score()
					))
					.orElse(null)
				)
				.filter(Objects::nonNull)
				.toList();

		recommendationPolicyRepository.saveAll(recommendationPolicies);

		return new RecommendationResponse(
			savedRecommend.getId(),
			savedRecommend.getAnswer(),
			validReferences
		);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RecommendationHistoryResponse> findMyRecommendations(Long userId) {
		return recommendationRepository.findByUserIdOrderByCreatedAtDesc(userId)
			.stream()
			.map(RecommendationHistoryResponse::from)
			.toList();
	}

	@Override
	public RecommendationDetailResponse findRecommendationDetail(Long userId, Long recommendationId) {

		RecommendationEntity recommendation = recommendationRepository
			.findByIdAndUserId(recommendationId, userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		List<RecommendationPolicyEntity> policies =
			recommendationPolicyRepository.findByRecommendation(recommendation);

		return RecommendationDetailResponse.from(
			recommendation,
			policies
		);
	}

	@Override
	public AiChatResponse chatWithHistory(
		Long userId,
		String message,
		List<MessageEntity> recentMessages
	) {
		userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		UsersProfileEntity profile = profileRepository.findByUsersId(userId)
			.orElse(null);

		List<AiConversationRequest.AiMessage> messages = recentMessages.stream()
			.map(chatMessage -> new AiConversationRequest.AiMessage(
				chatMessage.getRole().name(),
				chatMessage.getContent()
			))
			.toList();

		AiConversationRequest aiRequest = new AiConversationRequest(
			message,
			AiConversationRequest.UserProfilePayload.from(profile),
			messages
		);

		AiChatResponse aiResponse = webClient.post()
			.uri(aiServerUrl + "/api/v1/ai/chat")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(aiRequest)
			.retrieve()
			.bodyToMono(AiChatResponse.class)
			.block();

		if (aiResponse == null) {
			throw new BusinessException(ErrorCode.AI_RESPONSE_FAILED);
		}

		return new AiChatResponse(
			aiResponse.answer(),
			filterExistingPolicyReferences(aiResponse.references())
		);
	}

	private List<AiChatResponse.PolicyReferenceDto> filterExistingPolicyReferences(
		List<AiChatResponse.PolicyReferenceDto> references
	) {
		if (references == null) {
			return List.of();
		}

		return references.stream()
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
	}
}
