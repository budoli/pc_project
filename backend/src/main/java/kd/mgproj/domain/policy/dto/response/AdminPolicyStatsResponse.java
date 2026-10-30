package kd.mgproj.domain.policy.dto.response;

/**
 * 관리자 화면용 정책 색인 현황
 * @param vectorCount AI 서버에 연결할 수 없으면 null
 */
public record AdminPolicyStatsResponse(
	long policyCount,
	long documentCount,
	Long vectorCount
) {
}
