package kd.mgproj.domain.embedding.dto.request;

public record EmbeddingPolicyRequest(
	Long policyId,
	String title,
	String description,
	String organization,
	String region,
	String category

) {
}


