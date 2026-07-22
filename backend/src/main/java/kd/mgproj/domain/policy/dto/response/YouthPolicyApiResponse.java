package kd.mgproj.domain.policy.dto.response;

import kd.mgproj.domain.policy.dto.YouthPolicyResult;

public record YouthPolicyApiResponse(
	int resultCode,
	String resultMessage,
	YouthPolicyResult result
) {
}
