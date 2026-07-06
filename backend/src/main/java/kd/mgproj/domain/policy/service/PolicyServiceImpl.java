package kd.mgproj.domain.policy.service;

import kd.mgproj.common.enums.JobStatus;
import kd.mgproj.common.enums.PolicyCategory;
import kd.mgproj.domain.policy.dto.response.PolicyListResponseDto;
import kd.mgproj.domain.policy.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService{
    private final PolicyRepository policyRepository;

    @Override
    public List<PolicyListResponseDto> searchPolicies(String keyword, String region, PolicyCategory category, Integer age, String jobCondition) {
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
