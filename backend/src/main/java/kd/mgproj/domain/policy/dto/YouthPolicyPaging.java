package kd.mgproj.domain.policy.dto;

public record YouthPolicyPaging(
	int totCount,
	int pageNum,
	int pageSize
) {
}
