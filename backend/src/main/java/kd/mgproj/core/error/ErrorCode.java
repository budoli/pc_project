package kd.mgproj.core.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 애플리케이션 전역에서 사용하는 에러 코드 정의.
 *
 * <p>
 * 예외 발생 시 HTTP 상태 코드, 에러 코드,
 * 사용자에게 전달할 메시지를 관리
 * </p>
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
	// 공통 에러 (COMMON)
	NOT_FOUND(404, "COMMON-404", "요청한 리소스를 찾을 수 없습니다"),
	INTER_SERVER_ERROR(500, "COMMON-500", "서버 내부 오류가 발생했습니다"),
	EXTERNAL_API_FAILED(502, "COMMON-502", "외부 API 호출에 실패했습니다"),
	METHOD_ARGUMENT_NOT_VALID(400, "COMMON-400", "잘못된 요청 파라미터입니다"),

	// 인증/인가 에러 (AUTH)
	UNAUTHORIZED(401, "AUTH-401", "인증이 필요합니다"),
	INVALID_REFRESH_TOKEN(401, "AUTH-401", "유효하지 않은 리프레시 토큰입니다"),
	INVALID_ACCESS_TOKEN(401, "AUTH-401", "유효하지 않은 액세스 토큰입니다"),
	TOKEN_EXPIRED(401, "AUTH-401", "토큰이 만료되었습니다"),
	INVALID_PASSWORD(401, "AUTH-401", "잘못된 비밀번호입니다"),

	// 사용자 관련 에러 (USER)
	USER_NOT_FOUND(404, "USER-404", "사용자를 찾을 수 없습니다"),
	DUPLICATE_USER(409, "USER-409", "이미 존재하는 사용자입니다"),
	DUPLICATE_EMAIL(409, "USER-409", "이미 등록된 이메일입니다. 다른 이메일을 사용해주세요"),
	INVALID_EMAIL_VERIFICATION_CODE(401, "USER-401", "유효하지 않은 이메일 인증 코드입니다"),
	EMAIL_CODE_TIMEOUT(410, "USER-410", "이메일 인증 코드가 만료되었습니다"),
	EMAIL_VERIFICATION_REQUEST_NOT_FOUND(400, "USER-400", "이메일 인증 요청을 찾을 수 없습니다"),

	// 파일 업로드 에러 (FILE)
	EMPTY_FILE(400, "FILE-400", "파일이 비어있습니다"),
	MISSING_FILE_EXTENSION(400, "FILE-400", "파일 확장자가 없습니다. 유효한 확장자를 포함한 파일을 업로드해주세요"),
	INVALID_FILE_EXTENSION(400, "FILE-400", "지원하지 않는 파일 확장자입니다. jpg, png, gif, jpeg 파일만 업로드 가능합니다"),
	INVALID_IMAGE_FILE(400, "FILE-400", "유효하지 않은 이미지 파일입니다"),
	FILE_UPLOAD_FAILED(500, "FILE-500", "파일 업로드에 실패했습니다"),

	//JWT
	INVALID_JWT(401, "AUTH-401", "유효하지 않은 JWT 토큰입니다"),
	JWT_EXPIRED(401, "AUTH-401", "JWT 토큰이 만료되었습니다"),
	JWT_SIGNATURE_INVALID(401, "AUTH-401", "JWT 서명이 유효하지 않습니다"),
	JWT_UNSUPPORTED(401, "AUTH-401", "지원되지 않는 JWT 토큰입니다"),
	JWT_EMPTY(400, "AUTH-400", "토큰이 비어있습니다"),

	// 토큰 재사용 감지
	REFRESH_TOKEN_REUSED(401, "AUTH-401-TOKEN-REUSE", "비정상적인 토큰 사용이 감지되었습니다"),

	// Email 인증
	EMAIL_CODE_EXPIRED(400, "EMAIL-400", "인증번호가 만료되었습니다."),
	EMAIL_CODE_MISMATCH(400, "EMAIL-401", "인증번호가 일치하지 않습니다."),
	EMAIL_NOT_VERIFIED(400, "EMAIL-402", "인증이 완료되지 않았습니다."),

	//AI
	AI_RESPONSE_FAILED(502, "AI-502", "AI 응답 생성에 실패했습니다."),

	//CHAT
	CONVERSATION_NOT_FOUND(404, "CHAT-404", "대화를 찾을 수 없습니다."),
	EMPTY_MESSAGE(400, "CHAT-400", "메시지를 입력해주세요.");

	private final int status;
	private final String errorCode;
	private final String message;
}


