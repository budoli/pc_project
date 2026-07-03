package kd.mgproj.domain.policy.controller;

import kd.mgproj.common.enums.PolicyCategory;
import kd.mgproj.domain.policy.dto.response.PolicyListResponseDto;
import kd.mgproj.domain.policy.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/policies")
public class PolicyController {

    private final PolicyService policyService;

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
