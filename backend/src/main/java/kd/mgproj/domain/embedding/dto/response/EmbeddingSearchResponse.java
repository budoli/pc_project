package kd.mgproj.domain.embedding.dto.response;

public record EmbeddingSearchResponse(
	Double score,
	Long policyId,
	String title,
	String description,
	String organization,
	String region,
	String category
) {
}
