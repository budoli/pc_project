package kd.mgproj.domain.policy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClientException;

import kd.mgproj.domain.embedding.service.EmbeddingService;
import kd.mgproj.domain.policy.client.YouthPolicyApiClient;
import kd.mgproj.domain.policy.dto.YouthPolicyItem;
import kd.mgproj.domain.policy.dto.YouthPolicyPaging;
import kd.mgproj.domain.policy.dto.YouthPolicyResult;
import kd.mgproj.domain.policy.dto.response.PolicySyncResponse;
import kd.mgproj.domain.policy.dto.response.YouthPolicyApiResponse;
import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.policy.entity.PolicyConditionsEntity;
import kd.mgproj.domain.policy.entity.PolicyDocumentsEntity;
import kd.mgproj.domain.policy.repository.PolicyConditionsRepository;
import kd.mgproj.domain.policy.repository.PolicyDocumentsRepository;
import kd.mgproj.domain.policy.repository.PolicyRepository;
import tools.jackson.databind.ObjectMapper;

class PolicySyncServiceImplTest {
	private static final String RAW_DATA = "{}";
	private static final String HASH = PolicySyncServiceImpl.sha256(RAW_DATA);

	private final ObjectMapper objectMapper = mock(ObjectMapper.class);
	private final YouthPolicyApiClient apiClient = mock(YouthPolicyApiClient.class);
	private final PolicyRepository policyRepository = mock(PolicyRepository.class);
	private final EmbeddingService embeddingService = mock(EmbeddingService.class);
	private final PolicyDocumentsRepository documentsRepository = mock(PolicyDocumentsRepository.class);
	private final PolicyConditionsRepository conditionsRepository = mock(PolicyConditionsRepository.class);
	private final PolicySyncServiceImpl service = new PolicySyncServiceImpl(
		objectMapper, apiClient, policyRepository, embeddingService,
		documentsRepository, conditionsRepository,
		new TransactionTemplate(mock(PlatformTransactionManager.class))
	);
	private final YouthPolicyItem item = mock(YouthPolicyItem.class);
	private final PoliciesEntity policy = mock(PoliciesEntity.class);
	private final PolicyConditionsEntity condition = mock(PolicyConditionsEntity.class);
	private final PolicyDocumentsEntity document = mock(PolicyDocumentsEntity.class);

	@BeforeEach
	void setUp() {
		when(item.plcyNo()).thenReturn("external-id");
		when(objectMapper.writeValueAsString(item)).thenReturn(RAW_DATA);
		when(apiClient.fetchPolicies(1, 100)).thenReturn(new YouthPolicyApiResponse(
			0, "ok", new YouthPolicyResult(new YouthPolicyPaging(1, 1, 100), List.of(item))
		));

		when(policy.getId()).thenReturn(1L);
		when(policy.getExternalId()).thenReturn("external-id");
		when(condition.getPolicy()).thenReturn(policy);
		when(document.getPolicy()).thenReturn(policy);
		when(conditionsRepository.findAllByPolicyIn(anyCollection())).thenReturn(List.of(condition));
		when(documentsRepository.findAllByPolicyIn(anyCollection())).thenReturn(List.of(document));
	}

	@Test
	void skipsUnchangedPolicyThatIsAlreadyEmbedded() {
		givenExistingPolicy(HASH, false);

		PolicySyncResponse result = service.syncPolicies();

		assertEquals(1, result.unchanged());
		assertEquals(0, result.updated());
		verify(policy, never()).updateFromYouthPolicy(any(), any(), any());
		verifyNoInteractions(embeddingService);
	}

	@Test
	void updatesAndEmbedsChangedPolicy() {
		givenExistingPolicy("old-hash", true);
		when(policy.updateFromYouthPolicy(item, RAW_DATA, HASH)).thenReturn(policy);

		PolicySyncResponse result = service.syncPolicies();

		assertEquals(1, result.updated());
		assertEquals(1, result.embedded());
		verify(condition).updateFrom(item);
		verify(document).updateFrom(item);
		verify(embeddingService).embedPolicies(List.of(policy));
		verify(policyRepository).markEmbedded(List.of(1L));
	}

	@Test
	void retriesUnchangedPolicyWhoseEmbeddingFailedBefore() {
		givenExistingPolicy(HASH, true);

		PolicySyncResponse result = service.syncPolicies();

		assertEquals(1, result.unchanged());
		assertEquals(1, result.embedded());
		verify(embeddingService).embedPolicies(List.of(policy));
	}

	@Test
	void keepsSyncingWhenEmbeddingFails() {
		givenExistingPolicy("old-hash", true);
		when(policy.updateFromYouthPolicy(item, RAW_DATA, HASH)).thenReturn(policy);
		doThrow(new RestClientException("AI unavailable")).when(embeddingService).embedPolicies(anyList());

		PolicySyncResponse result = service.syncPolicies();

		assertEquals(1, result.updated());
		assertEquals(1, result.embeddingFailed());
		verify(policyRepository, never()).markEmbedded(anyList());
	}

	private void givenExistingPolicy(String contentHash, boolean needsEmbedding) {
		when(policy.getContentHash()).thenReturn(contentHash);
		when(policy.needsEmbedding()).thenReturn(needsEmbedding);
		when(policyRepository.findAllByExternalIdIn(List.of("external-id"))).thenReturn(List.of(policy));
	}
}
