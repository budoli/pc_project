package kd.mgproj.domain.policy.service;

import java.util.List;

import kd.mgproj.common.enums.PolicyCategory;
import kd.mgproj.domain.policy.dto.response.PolicyDetailResponse;
import kd.mgproj.domain.policy.dto.response.PolicyListResponseDto;

public interface PolicyService {
	PolicyDetailResponse getPolicyDetail(Long policyId);

	List<PolicyListResponseDto> searchPolicies(
		String keyword,
		String region,
		PolicyCategory category,
		Integer age,
		String jobCondition
	);
}
