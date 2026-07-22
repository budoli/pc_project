package kd.mgproj.domain.policy.dto.response;

import java.time.LocalDate;

import kd.mgproj.common.enums.PolicyCategory;
import kd.mgproj.domain.policy.entity.PoliciesEntity;

public record PolicyListResponseDto(
	Long id,
	String title,
	String organization,
	String region,
	PolicyCategory category,
	LocalDate applyEndDate
) {
	public static PolicyListResponseDto from(PoliciesEntity policy) {
		return new PolicyListResponseDto(
			policy.getId(),
			policy.getTitle(),
			policy.getOrganization(),
			policy.getRegion(),
			policy.getCategory(),
			policy.getApplyEndDate()
		);
	}
}

