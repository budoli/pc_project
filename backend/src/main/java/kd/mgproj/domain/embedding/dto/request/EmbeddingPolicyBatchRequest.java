package kd.mgproj.domain.embedding.dto.request;

import java.util.List;

public record EmbeddingPolicyBatchRequest(
        List<EmbeddingPolicyRequest> policies
) {}


