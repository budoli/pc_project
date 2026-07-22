package kd.mgproj.domain.policy.dto.response;

import java.time.LocalDate;

import kd.mgproj.common.enums.PolicyCategory;
import kd.mgproj.common.enums.PolicyStatus;
import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.policy.entity.PolicyConditionsEntity;
import kd.mgproj.domain.policy.entity.PolicyDocumentsEntity;

public record PolicyDetailResponse(
	Long id,
	String externalId,
	String title,
	String description,
	String organization,
	LocalDate applyStartDate,
	LocalDate applyEndDate,
	String region,
	PolicyCategory category,
	String sourceUrl,
	PolicyStatus status,
	Condition condition,
	Document document
) {
	public static PolicyDetailResponse from(
		PoliciesEntity policy,
		PolicyConditionsEntity condition,
		PolicyDocumentsEntity document
	) {
		return new PolicyDetailResponse(
			policy.getId(),
			policy.getExternalId(),
			policy.getTitle(),
			policy.getDescription(),
			policy.getOrganization(),
			policy.getApplyStartDate(),
			policy.getApplyEndDate(),
			policy.getRegion(),
			policy.getCategory(),
			policy.getSourceUrl(),
			policy.getStatus(),
			condition == null ? null : Condition.from(condition),
			document == null ? null : Document.from(document)
		);
	}

	public record Condition(
		Integer minAge,
		Integer maxAge,
		String region,
		String incomeCondition,
		String jobCondition,
		String educationCondition,
		String additionalCondition,
		String excludedTarget
	) {
		public static Condition from(PolicyConditionsEntity condition) {
			return new Condition(
				condition.getMinAge(),
				condition.getMaxAge(),
				condition.getRegion(),
				condition.getIncomeCondition(),
				condition.getJobCondition(),
				condition.getEducationCondition(),
				condition.getAdditionalCondition(),
				condition.getExcludedTarget()
			);
		}
	}

	public record Document(
		String sourceType,
		String rawText,
		String summary
	) {
		public static Document from(PolicyDocumentsEntity document) {
			return new Document(
				document.getSourceType(),
				document.getRawText(),
				document.getSummary()
			);
		}
	}
}