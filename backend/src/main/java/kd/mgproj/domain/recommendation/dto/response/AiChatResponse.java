package kd.mgproj.domain.recommendation.dto.response;

import java.util.List;

public record AiChatResponse(
	String answer,
	List<PolicyReferenceDto> references
) {
	public record PolicyReferenceDto(
		Long policyId,
		String title,
		Double score
	) {
	}
}
