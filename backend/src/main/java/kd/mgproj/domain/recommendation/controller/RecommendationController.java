package kd.mgproj.domain.recommendation.controller;

import io.swagger.v3.oas.annotations.Operation;
import kd.mgproj.core.security.UserDetailsImpl;
import kd.mgproj.domain.recommendation.dto.request.RecommendationRequest;
import kd.mgproj.domain.recommendation.dto.response.RecommendationDetailResponse;
import kd.mgproj.domain.recommendation.dto.response.RecommendationHistoryResponse;
import kd.mgproj.domain.recommendation.dto.response.RecommendationResponse;
import kd.mgproj.domain.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    @Operation(summary = "AI 추천 API/맞춤 정책 추천 요청")
    public ResponseEntity<RecommendationResponse> recommend(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody RecommendationRequest request) {

        Long userId = userDetails.getUserId();

        return ResponseEntity.ok(recommendationService.recommend(userId, request));
    }

    @GetMapping
    @Operation(summary = "AI 추천 API/추천 결과 조회")
    public ResponseEntity<List<RecommendationHistoryResponse>> getMyRecommendations(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(
                recommendationService.findMyRecommendations(userDetails.getUserId())
        );
    }

    @GetMapping("/{recommendationId}")
    @Operation(summary = "AI 추천 API/추천 결과 상세 조회")
    public ResponseEntity<RecommendationDetailResponse> getDetailRecommendations (
            @PathVariable Long recommendationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(
                recommendationService.findRecommendationDetail(
                        userDetails.getUserId(),
                        recommendationId
                )
        );
    }
}
