package kd.mgproj.domain.policy.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kd.mgproj.domain.embedding.service.EmbeddingService;
import kd.mgproj.domain.policy.dto.response.AdminPolicyStatsResponse;
import kd.mgproj.domain.policy.repository.PolicyDocumentsRepository;
import kd.mgproj.domain.policy.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PolicyStatsService {
	private final PolicyRepository policyRepository;
	private final PolicyDocumentsRepository policyDocumentsRepository;
	private final EmbeddingService embeddingService;

	@Transactional(readOnly = true)
	public AdminPolicyStatsResponse getStats() {
		return new AdminPolicyStatsResponse(
			policyRepository.count(),
			policyDocumentsRepository.count(),
			embeddingService.countVectors()
		);
	}
}
