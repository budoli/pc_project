package kd.mgproj.domain.recommendation.service;

import kd.mgproj.core.error.ErrorCode;
import kd.mgproj.core.error.exception.BusinessException;
import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.policy.repository.PolicyRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final RestClient restClient;
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final RecommendationRepository recommendationRepository;
    private final RecommendationPolicyRepository recommendationPolicyRepository;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Override
    public RecommendationResponse recommend(Long userId, RecommendationRequest request) {
        UsersEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        AiChatResponse aiResponse = restClient.post()
                .uri(aiServerUrl + "/api/v1/ai/chat")
                .body(request)
                .retrieve()
                .body(AiChatResponse.class);

        RecommendationEntity recommendation = RecommendationEntity.create(
                user,
                request.query(),
                aiResponse.answer()
        );

        RecommendationEntity savedRecommend =
                recommendationRepository.save(recommendation);

        List<RecommendationPolicyEntity> recommendationPolicy =
                aiResponse.references().stream()
                        .filter(reference -> reference.policyId() != null)
                        .map(reference -> {
                            PoliciesEntity policy = policyRepository.findById(reference.policyId())
                                    .orElseThrow();

                            return RecommendationPolicyEntity.create(
                                    savedRecommend,
                                    policy,
                                    reference.score()
                            );
                        })
                        .toList();

        recommendationPolicyRepository.saveAll(recommendationPolicy);

        return new

                RecommendationResponse(
                savedRecommend.getId(),
                savedRecommend.getAnswer(),
                aiResponse.references()
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
}
