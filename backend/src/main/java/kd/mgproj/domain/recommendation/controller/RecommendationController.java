package kd.mgproj.domain.recommendation.controller;

import io.swagger.v3.oas.annotations.Operation;
import kd.mgproj.core.security.UserDetailsImpl;
import kd.mgproj.domain.recommendation.dto.request.RecommendationRequest;
import kd.mgproj.domain.recommendation.dto.response.RecommendationResponse;
import kd.mgproj.domain.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
