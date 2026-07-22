package kd.mgproj.domain.embedding.dto.request;

public record EmbeddingSearchRequest(
	String query,
	int limit
) {
}


