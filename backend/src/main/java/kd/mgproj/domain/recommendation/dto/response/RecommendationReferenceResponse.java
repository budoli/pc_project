package kd.mgproj.domain.recommendation.dto.response;

import kd.mgproj.domain.recommendation.entity.RecommendationPolicyEntity;

public record RecommendationReferenceResponse(
        Long policyId,
        String title,
        Double score
) {
    public static RecommendationReferenceResponse from(
            RecommendationPolicyEntity entity
    ) {
        return new RecommendationReferenceResponse(
                entity.getPolicy().getId(),
                entity.getPolicy().getTitle(),
                entity.getScore()
        );
    }
}