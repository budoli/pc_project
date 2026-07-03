package kd.mgproj.domain.policy.service;

import kd.mgproj.common.enums.PolicyCategory;
import kd.mgproj.domain.policy.dto.response.PolicyListResponseDto;

import java.util.List;

public interface PolicyService {
    List<PolicyListResponseDto> searchPolicies(
            String keyword,
            String region,
            PolicyCategory category,
            Integer age,
            String jobCondition
    );
}
