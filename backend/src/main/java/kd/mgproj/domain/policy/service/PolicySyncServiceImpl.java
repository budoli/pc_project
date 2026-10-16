package kd.mgproj.domain.policy.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestClientException;

import jakarta.transaction.Transactional;
import kd.mgproj.core.error.ErrorCode;
import kd.mgproj.core.error.exception.BusinessException;
import kd.mgproj.domain.embedding.service.EmbeddingService;
import kd.mgproj.domain.policy.client.YouthPolicyApiClient;
import kd.mgproj.domain.policy.dto.YouthPolicyItem;
import kd.mgproj.domain.policy.dto.response.YouthPolicyApiResponse;
import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.policy.entity.PolicyConditionsEntity;
import kd.mgproj.domain.policy.entity.PolicyDocumentsEntity;
import kd.mgproj.domain.policy.repository.PolicyConditionsRepository;
import kd.mgproj.domain.policy.repository.PolicyDocumentsRepository;
import kd.mgproj.domain.policy.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicySyncServiceImpl implements PolicySyncService {

	private final ObjectMapper objectMapper;
	private final YouthPolicyApiClient apiClient;
	private final PolicyRepository policyRepository;
	private final EmbeddingService embeddingService;
	private final PolicyDocumentsRepository policyDocumentsRepository;
	private final PolicyConditionsRepository policyConditionsRepository;

	@Override
	@Transactional
	public int syncPolicies() {
		int pageNum = 1;
		int pageSize = 100;
		int syncedCount = 0;

		List<PoliciesEntity> savedPolicies = new ArrayList<>();

		while (true) {
			YouthPolicyApiResponse response =
				apiClient.fetchPolicies(pageNum, pageSize);

			if (response == null || response.result() == null) {
				break;
			}

			List<YouthPolicyItem> items = response.result().youthPolicyList();

			if (items == null || items.isEmpty()) {
				break;
			}

			for (YouthPolicyItem item : items) {
				String rawData = toJson(item);

				PoliciesEntity policy = policyRepository.findByExternalId(item.plcyNo())
					.map(existing -> existing.updateFromYouthPolicy(item, rawData))
					.orElseGet(() -> PoliciesEntity.fromYouthPolicy(item, rawData));

				PoliciesEntity savedPolicy = policyRepository.save(policy);

				syncCondition(savedPolicy, item);
				syncDocument(savedPolicy, item);

				savedPolicies.add(savedPolicy);
				syncedCount++;
			}

			int totalCount = response.result().pagging() == null
				? Integer.MAX_VALUE
				: response.result().pagging().totCount();

			if (items.size() < pageSize || syncedCount >= totalCount) {
				break;
			}

			pageNum++;
		}

		if (!savedPolicies.isEmpty()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					try {
						embeddingService.embedPolicies(savedPolicies);
					} catch (RestClientException exception) {
						log.error("Policy embedding failed after database commit", exception);
						throw new BusinessException(ErrorCode.EXTERNAL_API_FAILED);
					}
				}
			});
		}

		return syncedCount;
	}

	@Override
	@Transactional
	public int reindexPolicies() {
		List<PoliciesEntity> policies = policyRepository.findAll();
		embeddingService.embedPolicies(policies);
		return policies.size();
	}

	private void syncCondition(
		PoliciesEntity policy,
		YouthPolicyItem item
	) {
		PolicyConditionsEntity condition = policyConditionsRepository.findByPolicy(policy)
			.orElseGet(() -> PolicyConditionsEntity.from(policy, item));

		condition.updateFrom(item);

		policyConditionsRepository.save(condition);
	}

	private void syncDocument(
		PoliciesEntity policy,
		YouthPolicyItem item
	) {
		PolicyDocumentsEntity document = policyDocumentsRepository.findByPolicy(policy)
			.orElseGet(() -> PolicyDocumentsEntity.from(policy, item));

		document.updateFrom(item);

		policyDocumentsRepository.save(document);
	}

	private String toJson(YouthPolicyItem item) {
		return objectMapper.writeValueAsString(item);
	}
}
