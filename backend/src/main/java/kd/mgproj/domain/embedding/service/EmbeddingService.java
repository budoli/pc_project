package kd.mgproj.domain.embedding.service;

import kd.mgproj.domain.embedding.dto.request.EmbeddingPolicyBatchRequest;
import kd.mgproj.domain.embedding.dto.request.EmbeddingPolicyRequest;
import kd.mgproj.domain.embedding.dto.request.EmbeddingSearchRequest;
import kd.mgproj.domain.embedding.dto.response.EmbeddingSearchResponse;
import kd.mgproj.domain.policy.entity.PoliciesEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final RestClient restClient;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public void embedPolicies(List<PoliciesEntity> policies) {
        List<EmbeddingPolicyRequest> request = policies.stream()
                .map(policy -> new EmbeddingPolicyRequest(
                        policy.getId(),
                        policy.getTitle(),
                        policy.getDescription(),
                        policy.getOrganization(),
                        policy.getRegion(),
                        policy.getCategory().name()
                ))
                .toList();

        EmbeddingPolicyBatchRequest batchRequest =
                new EmbeddingPolicyBatchRequest(request);


        restClient.post()
                .uri(aiServerUrl + "/api/v1/embedding/policies")
                .body(batchRequest)
                .retrieve()
                .toBodilessEntity();
    }

    public List<EmbeddingSearchResponse> searchPolicies(String query, int limit) {
        EmbeddingSearchRequest request = new EmbeddingSearchRequest(query, limit);

        return restClient.post()
                .uri(aiServerUrl + "/api/v1/embeddings/search")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {}
                );
    }

}
