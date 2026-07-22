package kd.mgproj.domain.policy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kd.mgproj.common.enums.PolicyCategory;
import kd.mgproj.core.error.ErrorCode;
import kd.mgproj.core.error.exception.BusinessException;
import kd.mgproj.domain.policy.dto.response.PolicyDetailResponse;
import kd.mgproj.domain.policy.dto.response.PolicyListResponseDto;
import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.policy.entity.PolicyConditionsEntity;
import kd.mgproj.domain.policy.entity.PolicyDocumentsEntity;
import kd.mgproj.domain.policy.repository.PolicyConditionsRepository;
import kd.mgproj.domain.policy.repository.PolicyDocumentsRepository;
import kd.mgproj.domain.policy.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {
	private final PolicyRepository policyRepository;
	private final PolicyConditionsRepository conditionRepository;
	private final PolicyDocumentsRepository documentRepository;

	@Override
	@Transactional
	public PolicyDetailResponse getPolicyDetail(Long policyId) {
		PoliciesEntity policy = policyRepository.findById(policyId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		PolicyConditionsEntity condition = conditionRepository.findByPolicyId(policyId)
			.orElse(null);

		PolicyDocumentsEntity documents = documentRepository.findByPolicyId(policyId)
			.orElse(null);

		return PolicyDetailResponse.from(policy, condition, documents);
	}

	@Override
	@Transactional
	public List<PolicyListResponseDto> searchPolicies(String keyword, String region, PolicyCategory category,
		Integer age, String jobCondition) {
		return policyRepository.searchPolicies(
				keyword,
				region,
				category,
				age,
				jobCondition

			)
			.stream()
			.map(PolicyListResponseDto::from)
			.toList();
	}
}
