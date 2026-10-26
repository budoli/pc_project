package kd.mgproj.domain.policy.dto.response;

/**
 * 정책 동기화 결과
 * @param unchanged       원본 응답 해시가 같아 DB 갱신을 건너뛴 정책 수
 * @param embedded        이번 동기화에서 임베딩한 정책 수 (신규·변경, 이전 임베딩 실패분)
 * @param embeddingFailed 임베딩에 실패한 정책 수 (다음 동기화 때 다시 임베딩된다)
 */
public record PolicySyncResponse(
	int total,
	int created,
	int updated,
	int unchanged,
	int embedded,
	int embeddingFailed,
	long elapsedMs
) {
}
