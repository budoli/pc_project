package kd.mgproj.domain.policy.dto;

import java.util.List;

public record YouthPolicyResult(
	YouthPolicyPaging pagging,
	List<YouthPolicyItem> youthPolicyList
) {
}
