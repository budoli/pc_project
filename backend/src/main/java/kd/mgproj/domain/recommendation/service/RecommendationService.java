package kd.mgproj.domain.recommendation.service;

import kd.mgproj.domain.recommendation.dto.request.RecommendationRequest;
import kd.mgproj.domain.recommendation.dto.response.RecommendationResponse;

public interface RecommendationService {
    RecommendationResponse recommend(Long userId, RecommendationRequest request);

}
