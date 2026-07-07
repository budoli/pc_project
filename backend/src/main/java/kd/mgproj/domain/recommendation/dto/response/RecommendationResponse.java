package kd.mgproj.domain.recommendation.dto.response;

import java.util.List;

public record RecommendationResponse(
        Long recommendationId,
        String answer,
        List<AiChatResponse.PolicyReferenceDto> references
) {
}
