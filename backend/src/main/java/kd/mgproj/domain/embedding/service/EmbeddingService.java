package kd.mgproj.domain.embedding.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import kd.mgproj.domain.embedding.dto.request.EmbeddingPolicyBatchRequest;
import kd.mgproj.domain.embedding.dto.request.EmbeddingPolicyRequest;
import kd.mgproj.domain.embedding.dto.request.EmbeddingSearchRequest;
import kd.mgproj.domain.embedding.dto.response.EmbeddingSearchResponse;
import kd.mgproj.domain.embedding.dto.response.EmbeddingStatsResponse;
import kd.mgproj.domain.policy.entity.PoliciesEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {
	private static final int EMBEDDING_BATCH_SIZE = 25;

	private final RestClient restClient;

	@Value("${ai.server.url}")
	private String aiServerUrl;

	public void embedPolicies(List<PoliciesEntity> policies) {
		List<EmbeddingPolicyRequest> requests = policies.stream()
			.map(policy -> new EmbeddingPolicyRequest(
				policy.getId(),
				policy.getTitle(),
				policy.getDescription(),
				policy.getOrganization(),
				policy.getRegion(),
				policy.getCategory().name()
			))
			.toList();

		for (int start = 0; start < requests.size(); start += EMBEDDING_BATCH_SIZE) {
			int end = Math.min(start + EMBEDDING_BATCH_SIZE, requests.size());
			EmbeddingPolicyBatchRequest batchRequest =
				new EmbeddingPolicyBatchRequest(requests.subList(start, end));

			restClient.post()
				.uri(aiServerUrl + "/api/v1/ai/embeddings/policies/batch")
				.body(batchRequest)
				.retrieve()
				.toBodilessEntity();
		}
	}

	/**
	 * Qdrant에 저장된 정책 벡터 수
	 * @return AI 서버에 연결할 수 없으면 null
	 */
	public Long countVectors() {
		try {
			EmbeddingStatsResponse response = restClient.get()
				.uri(aiServerUrl + "/api/v1/ai/embeddings/stats")
				.retrieve()
				.body(EmbeddingStatsResponse.class);

			return response == null ? null : response.vectorCount();
		} catch (RestClientException exception) {
			log.warn("Failed to fetch embedding stats: {}", exception.getMessage());
			return null;
		}
	}

	public List<EmbeddingSearchResponse> searchPolicies(String query, int limit) {
		EmbeddingSearchRequest request = new EmbeddingSearchRequest(query, limit);

		return restClient.post()
			.uri(aiServerUrl + "/api/v1/ai/search/similar-policies")
			.body(request)
			.retrieve()
			.body(new ParameterizedTypeReference<>() {
				  }
			);
	}

}
