package kd.mgproj.domain.recommendation.dto.response;

import kd.mgproj.domain.recommendation.entity.RecommendationEntity;
import kd.mgproj.domain.recommendation.entity.RecommendationPolicyEntity;

import java.util.List;

public record RecommendationDetailResponse(
        Long recommendationId,
        String question,
        String answer,
        List<RecommendationReferenceResponse> references
) {
    public static RecommendationDetailResponse from(
            RecommendationEntity recommendation,
            List<RecommendationPolicyEntity> policies
    ) {
        return new RecommendationDetailResponse(
                recommendation.getId(),
                recommendation.getQuestion(),
                recommendation.getAnswer(),
                policies.stream()
                        .map(RecommendationReferenceResponse::from)
                        .toList()
        );
    }
}


