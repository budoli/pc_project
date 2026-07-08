package kd.mgproj.domain.recommendation.service;

import kd.mgproj.domain.conversation.entity.MessageEntity;
import kd.mgproj.domain.recommendation.dto.request.RecommendationRequest;
import kd.mgproj.domain.recommendation.dto.response.AiChatResponse;
import kd.mgproj.domain.recommendation.dto.response.RecommendationDetailResponse;
import kd.mgproj.domain.recommendation.dto.response.RecommendationHistoryResponse;
import kd.mgproj.domain.recommendation.dto.response.RecommendationResponse;

import java.util.List;

public interface RecommendationService {
    RecommendationResponse recommend(Long userId, RecommendationRequest request);
    List<RecommendationHistoryResponse> findMyRecommendations(Long userId);
    RecommendationDetailResponse findRecommendationDetail(Long userId, Long recommendationId);
    AiChatResponse chatWithHistory(Long userId, String message, List<MessageEntity> recentMessages);

}
