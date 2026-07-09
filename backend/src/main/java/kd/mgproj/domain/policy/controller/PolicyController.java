package kd.mgproj.domain.policy.controller;

import io.swagger.v3.oas.annotations.Operation;
import kd.mgproj.common.enums.PolicyCategory;
import kd.mgproj.domain.policy.dto.response.PolicyDetailResponse;
import kd.mgproj.domain.policy.dto.response.PolicyListResponseDto;
import kd.mgproj.domain.policy.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/policies")
public class PolicyController {

    private final PolicyService policyService;


    @Operation(summary = "정책 API/정책 상세 조회")
    @GetMapping("/{policyId}")
    public ResponseEntity<PolicyDetailResponse> getPolicyDetail(
            @PathVariable Long policyId
    ) {
        return ResponseEntity.ok(policyService.getPolicyDetail(policyId));
    }

    @Operation(summary = "정책 API/정책 검색")
    @GetMapping("/search")
    public ResponseEntity<List<PolicyListResponseDto>> searchPolicies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) PolicyCategory category,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String jobCondition
    ) {
        return ResponseEntity.ok(
                policyService.searchPolicies(keyword, region, category, age, jobCondition)
        );
    }
}
