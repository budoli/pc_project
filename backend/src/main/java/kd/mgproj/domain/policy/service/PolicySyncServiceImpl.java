package kd.mgproj.domain.policy.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClientException;

import kd.mgproj.domain.embedding.service.EmbeddingService;
import kd.mgproj.domain.policy.client.YouthPolicyApiClient;
import kd.mgproj.domain.policy.dto.YouthPolicyItem;
import kd.mgproj.domain.policy.dto.response.PolicySyncResponse;
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
	private static final int PAGE_SIZE = 100;

	private final ObjectMapper objectMapper;
	private final YouthPolicyApiClient apiClient;
	private final PolicyRepository policyRepository;
	private final EmbeddingService embeddingService;
	private final PolicyDocumentsRepository policyDocumentsRepository;
	private final PolicyConditionsRepository policyConditionsRepository;
	private final TransactionTemplate transactionTemplate;

	/**
	 * 정책 동기화
	 * 페이지(100건)마다 저장 트랜잭션을 커밋한 뒤 그 페이지에서 임베딩이 필요한 정책만 임베딩한다.
	 * 원본 응답 해시가 같은 정책은 DB 갱신과 임베딩을 모두 건너뛴다.
	 */
	@Override
	public PolicySyncResponse syncPolicies() {
		long startedAt = System.currentTimeMillis();
		SyncCounter counter = new SyncCounter();
		int pageNum = 1;

		while (true) {
			YouthPolicyApiResponse response = apiClient.fetchPolicies(pageNum, PAGE_SIZE);

			if (response == null || response.result() == null) {
				break;
			}

			List<YouthPolicyItem> items = response.result().youthPolicyList();

			if (items == null || items.isEmpty()) {
				break;
			}

			List<PoliciesEntity> embeddingTargets = transactionTemplate.execute(status -> savePage(items, counter));
			embed(embeddingTargets, counter);

			int totalCount = response.result().pagging() == null
				? Integer.MAX_VALUE
				: response.result().pagging().totCount();

			if (items.size() < PAGE_SIZE || counter.total >= totalCount) {
				break;
			}

			pageNum++;
		}

		PolicySyncResponse result = counter.toResponse(System.currentTimeMillis() - startedAt);
		log.info("Policy sync finished: {}", result);
		return result;
	}

	/**
	 * 전체 재색인. 임베딩 중에는 트랜잭션을 열어두지 않는다.
	 */
	@Override
	public int reindexPolicies() {
		List<PoliciesEntity> policies = policyRepository.findAll();
		embeddingService.embedPolicies(policies);
		markEmbedded(policies);
		return policies.size();
	}

	/**
	 * 한 페이지 저장 (트랜잭션 안에서 실행)
	 * @return 이 페이지에서 임베딩이 필요한 정책 (신규·변경, 또는 이전 임베딩 실패)
	 */
	private List<PoliciesEntity> savePage(List<YouthPolicyItem> items, SyncCounter counter) {
		List<String> externalIds = items.stream().map(YouthPolicyItem::plcyNo).toList();
		Map<String, PoliciesEntity> policies = new HashMap<>(
			policyRepository.findAllByExternalIdIn(externalIds).stream()
				.collect(Collectors.toMap(PoliciesEntity::getExternalId, Function.identity()))
		);
		Map<Long, PolicyConditionsEntity> conditions = policies.isEmpty() ? new HashMap<>()
			: policyConditionsRepository.findAllByPolicyIn(policies.values()).stream()
				.collect(Collectors.toMap(condition -> condition.getPolicy().getId(), Function.identity()));
		Map<Long, PolicyDocumentsEntity> documents = policies.isEmpty() ? new HashMap<>()
			: policyDocumentsRepository.findAllByPolicyIn(policies.values()).stream()
				.collect(Collectors.toMap(document -> document.getPolicy().getId(), Function.identity()));

		List<PoliciesEntity> pagePolicies = new ArrayList<>();

		for (YouthPolicyItem item : items) {
			String rawData = toJson(item);
			String contentHash = sha256(rawData);
			PoliciesEntity existing = policies.get(item.plcyNo());
			counter.total++;

			if (existing != null && contentHash.equals(existing.getContentHash())) {
				counter.unchanged++;
				pagePolicies.add(existing);
				continue;
			}

			PoliciesEntity policy;
			if (existing == null) {
				policy = policyRepository.save(PoliciesEntity.fromYouthPolicy(item, rawData, contentHash));
				policies.put(policy.getExternalId(), policy);
				counter.created++;
			} else {
				policy = existing.updateFromYouthPolicy(item, rawData, contentHash);
				counter.updated++;
			}

			syncCondition(policy, item, conditions.get(policy.getId()));
			syncDocument(policy, item, documents.get(policy.getId()));
			pagePolicies.add(policy);
		}

		return pagePolicies.stream()
			.filter(PoliciesEntity::needsEmbedding)
			.distinct()
			.toList();
	}

	private void embed(List<PoliciesEntity> policies, SyncCounter counter) {
		if (policies == null || policies.isEmpty()) {
			return;
		}

		try {
			embeddingService.embedPolicies(policies);
			markEmbedded(policies);
			counter.embedded += policies.size();
		} catch (RestClientException exception) {
			// embeddedHash 가 갱신되지 않으므로 다음 동기화 때 다시 임베딩된다
			counter.embeddingFailed += policies.size();
			log.error("Policy embedding failed for {} policies", policies.size(), exception);
		}
	}

	private void markEmbedded(List<PoliciesEntity> policies) {
		List<Long> ids = policies.stream().map(PoliciesEntity::getId).toList();
		transactionTemplate.executeWithoutResult(status -> policyRepository.markEmbedded(ids));
	}

	private void syncCondition(PoliciesEntity policy, YouthPolicyItem item, PolicyConditionsEntity condition) {
		if (condition == null) {
			policyConditionsRepository.save(PolicyConditionsEntity.from(policy, item));
			return;
		}
		condition.updateFrom(item);
	}

	private void syncDocument(PoliciesEntity policy, YouthPolicyItem item, PolicyDocumentsEntity document) {
		if (document == null) {
			policyDocumentsRepository.save(PolicyDocumentsEntity.from(policy, item));
			return;
		}
		document.updateFrom(item);
	}

	private String toJson(YouthPolicyItem item) {
		return objectMapper.writeValueAsString(item);
	}

	static String sha256(String value) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(digest);
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException(exception);
		}
	}

	private static class SyncCounter {
		private int total;
		private int created;
		private int updated;
		private int unchanged;
		private int embedded;
		private int embeddingFailed;

		private PolicySyncResponse toResponse(long elapsedMs) {
			return new PolicySyncResponse(total, created, updated, unchanged, embedded, embeddingFailed, elapsedMs);
		}
	}
}
