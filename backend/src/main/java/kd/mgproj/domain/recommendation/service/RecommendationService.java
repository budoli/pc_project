package kd.mgproj.domain.recommendation.service;

import kd.mgproj.domain.recommendation.dto.request.RecommendationRequest;
import kd.mgproj.domain.recommendation.dto.response.RecommendationDetailResponse;
import kd.mgproj.domain.recommendation.dto.response.RecommendationHistoryResponse;
import kd.mgproj.domain.recommendation.dto.response.RecommendationResponse;

import java.util.List;

public interface RecommendationService {
    RecommendationResponse recommend(Long userId, RecommendationRequest request);
    List<RecommendationHistoryResponse> findMyRecommendations(Long userId);
    RecommendationDetailResponse findRecommendationDetail(Long userId, Long recommendationId);

}
