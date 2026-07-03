package kd.mgproj.domain.policy.service;

import jakarta.transaction.Transactional;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicySyncServiceImpl implements PolicySyncService {

    private final YouthPolicyApiClient apiClient;
    private final PolicyRepository policyRepository;
    private final PolicyConditionsRepository policyConditionsRepository;
    private final PolicyDocumentsRepository policyDocumentsRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public int syncPolicies() {
        int pageNum = 1;
        int pageSize = 100;
        int syncedCount = 0;

        while (true) {
            YouthPolicyApiResponse response =
                    apiClient.fetchPolicies(pageNum, pageSize);

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

                syncedCount++;
            }

            if (items.size() < pageSize) {
                break;
            }


            pageNum++;
        }

        return syncedCount;
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