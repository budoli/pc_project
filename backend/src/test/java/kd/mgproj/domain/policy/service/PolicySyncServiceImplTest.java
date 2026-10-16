package kd.mgproj.domain.policy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestClientException;

import kd.mgproj.core.error.ErrorCode;
import kd.mgproj.core.error.exception.BusinessException;
import kd.mgproj.domain.embedding.service.EmbeddingService;
import kd.mgproj.domain.policy.client.YouthPolicyApiClient;
import kd.mgproj.domain.policy.dto.YouthPolicyItem;
import kd.mgproj.domain.policy.dto.YouthPolicyPaging;
import kd.mgproj.domain.policy.dto.YouthPolicyResult;
import kd.mgproj.domain.policy.dto.response.YouthPolicyApiResponse;
import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.policy.entity.PolicyConditionsEntity;
import kd.mgproj.domain.policy.entity.PolicyDocumentsEntity;
import kd.mgproj.domain.policy.repository.PolicyConditionsRepository;
import kd.mgproj.domain.policy.repository.PolicyDocumentsRepository;
import kd.mgproj.domain.policy.repository.PolicyRepository;
import tools.jackson.databind.ObjectMapper;

class PolicySyncServiceImplTest {
	private final ObjectMapper objectMapper = mock(ObjectMapper.class);
	private final YouthPolicyApiClient apiClient = mock(YouthPolicyApiClient.class);
	private final PolicyRepository policyRepository = mock(PolicyRepository.class);
	private final EmbeddingService embeddingService = mock(EmbeddingService.class);
	private final PolicyDocumentsRepository documentsRepository = mock(PolicyDocumentsRepository.class);
	private final PolicyConditionsRepository conditionsRepository = mock(PolicyConditionsRepository.class);
	private final PolicySyncServiceImpl service = new PolicySyncServiceImpl(
		objectMapper, apiClient, policyRepository, embeddingService,
		documentsRepository, conditionsRepository
	);
	private final PoliciesEntity policy = mock(PoliciesEntity.class);

	@BeforeEach
	void setUp() {
		TransactionSynchronizationManager.initSynchronization();
		YouthPolicyItem item = mock(YouthPolicyItem.class);
		when(item.plcyNo()).thenReturn("external-id");
		when(objectMapper.writeValueAsString(item)).thenReturn("{}");
		when(apiClient.fetchPolicies(1, 100)).thenReturn(new YouthPolicyApiResponse(
			0, "ok", new YouthPolicyResult(new YouthPolicyPaging(1, 1, 100), List.of(item))
		));
		when(policyRepository.findByExternalId("external-id")).thenReturn(Optional.of(policy));
		when(policy.updateFromYouthPolicy(item, "{}")).thenReturn(policy);
		when(policyRepository.save(policy)).thenReturn(policy);
		when(conditionsRepository.findByPolicy(policy)).thenReturn(Optional.of(mock(PolicyConditionsEntity.class)));
		when(documentsRepository.findByPolicy(policy)).thenReturn(Optional.of(mock(PolicyDocumentsEntity.class)));
	}

	@AfterEach
	void tearDown() {
		TransactionSynchronizationManager.clearSynchronization();
	}

	@Test
	void embedsOnlyAfterDatabaseCommit() {
		assertEquals(1, service.syncPolicies());
		verifyNoInteractions(embeddingService);

		TransactionSynchronizationManager.getSynchronizations()
			.forEach(TransactionSynchronization::afterCommit);
		verify(embeddingService).embedPolicies(List.of(policy));
	}

	@Test
	void reportsEmbeddingFailureAfterCommit() {
		doThrow(new RestClientException("AI unavailable"))
			.when(embeddingService).embedPolicies(List.of(policy));
		service.syncPolicies();

		BusinessException exception = assertThrows(BusinessException.class, () ->
			TransactionSynchronizationManager.getSynchronizations()
				.forEach(TransactionSynchronization::afterCommit)
		);
		assertEquals(ErrorCode.EXTERNAL_API_FAILED, exception.getErrorCode());
	}
}
