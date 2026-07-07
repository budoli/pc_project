package kd.mgproj.domain.recommendation.dto.response;

import kd.mgproj.domain.recommendation.entity.RecommendationEntity;

import java.time.LocalDateTime;

public record RecommendationHistoryResponse(
        Long recommendationId,
        String question,
        LocalDateTime createdAt
) {
    public static RecommendationHistoryResponse from(RecommendationEntity entity) {
        return new RecommendationHistoryResponse(
                entity.getId(),
                entity.getQuestion(),
                entity.getCreatedAt()
        );
    }
}


