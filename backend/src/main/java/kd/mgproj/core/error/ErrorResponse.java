package kd.mgproj.core.error;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

/**
 * 예외 발생 시 클라이언트에 반환되는 표준 에러 응답 객체.
 *
 * <p>
 * 모든 예외 응답은 동일한 형식으로 반환
 * HTTP 상태 코드, 에러 코드, 메시지, 요청 경로,
 * 발생 시간 및 검증 오류 정보 포함
 * </p>
 *
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
	int status,
	String code,
	String message,
	LocalDateTime timestamp,
	List<FieldError> errors,
	String path
) {

	/**
	 * 일반 예외 응답 객체 생성
	 *
	 * <p>
	 * 필드 검증 오류가 없는 경우 사용,
	 * ErrorCode에 정의된 정보를 기반으로 응답 생성
	 * </p>
	 *
	 * @param errorCode 예외 코드 정보
	 * @param path      요청 URL
	 * @return ErrorResponse
	 */
	public static ErrorResponse of(ErrorCode errorCode, String path) {
		return ErrorResponse.builder()
			.status(errorCode.getStatus())
			.code(errorCode.getErrorCode())
			.message(errorCode.getMessage())
			.timestamp(LocalDateTime.now())
			.path(path)
			.build();
	}

	/**
	 * 유효성 검증 실패 정보를 포함한 예외 응답 객체 생성
	 *
	 * <p>
	 * Bean Validation(@Valid) 검증 실패 시
	 * 필드별 오류 목록을 포함하여 응답 생성
	 * </p>
	 *
	 * @param errorCode 예외 코드 정보
	 * @param errors    필드 검증 오류 목록
	 * @param path      요청 URL
	 * @return ErrorResponse
	 */
	public static ErrorResponse of(ErrorCode errorCode, List<FieldError> errors, String path) {
		return ErrorResponse.builder()
			.status(errorCode.getStatus())
			.code(errorCode.getErrorCode())
			.message(errorCode.getMessage())
			.timestamp(LocalDateTime.now())
			.errors(errors)
			.path(path)
			.build();
	}

	/**
	 * 입력값 검증 실패 시 필드별 오류 정보 저장.
	 *
	 * @param field  검증에 실패한 필드명
	 * @param value  입력된 값
	 * @param reason 검증 실패 사유
	 */
	public record FieldError(
		String field,
		String value,
		String reason
	) {
	}
}


