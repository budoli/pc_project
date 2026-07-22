package kd.mgproj.core.error;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import kd.mgproj.core.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 애플리케이션 전역 예외 처리 클래스
 *
 * <p>
 * Controller 계층에서 발생하는 예외를 일관된 형식의
 * ErrorResponse로 변환하여 클라이언트에 반환.
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	/**
	 * Bean Validation(@Valid) 검증 실패시 발생하는 예외 처리
	 *
	 * <p>
	 * Dto의 유효성 검증 과정에서 발생한 필드별 오류 정보를 수집하여
	 * 클라이언트가 어떤 필드에서 검증에 실패했는지 확인할 수 있도록
	 * ErrorResponse 형태로 반환.
	 * </p>
	 *
	 * @param e       MethodArgumentNotValidException
	 * @param request 현재 HTTP 요청 정보
	 * @return 400 Bad Request 응답
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e,
		HttpServletRequest request) {
		log.error("handleMethodArgumentNotValidException", e);

		List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult().getFieldErrors().stream()
			.map(error -> new ErrorResponse.FieldError(
				error.getField(),
				error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
				error.getDefaultMessage()))
			.toList();

		ErrorResponse response = ErrorResponse.of(ErrorCode.METHOD_ARGUMENT_NOT_VALID, fieldErrors,
			request.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * 비즈니스 로직 수행 중 발생한 사용자 정의 예외 처리
	 *
	 * <p>
	 * BusinessException 내부에 포함된 ErrorCode를 기반으로
	 * 상태 코드와 에러 메시지를 생성하여 반환
	 * </p>
	 *
	 * @param e       BusinessException
	 * @param request 현재 HTTP 요청 정보
	 * @return ErrorCode에 정의된 HTTP 상태 코드 응답
	 */
	@ExceptionHandler(BusinessException.class)
	protected ResponseEntity<ErrorResponse> handleBusinessException(
		BusinessException e,
		HttpServletRequest request) {

		log.warn("BusinessException: {}", e.getErrorCode().getMessage());

		ErrorCode errorCode = e.getErrorCode();
		ErrorResponse response = ErrorResponse.of(errorCode, request.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
	}

	/**
	 * 처리되지 않은 모든 예외를 처리하는 전역 예외 핸들러
	 *
	 * <p>
	 * 예상하지 못한 서버 내부 오류 발생 시
	 * 로그를 기록하고 표준화된 에러 응답을 반환.
	 * </p>
	 *
	 * @param e       발생한 예외
	 * @param request 현재 HTTP 요청 정보
	 * @return 500 Internal Server Error 응답
	 */
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleException(
		Exception e,
		HttpServletRequest request) {
		log.error("handleException: {}", e.getMessage(), e);

		ErrorResponse response = ErrorResponse.of(ErrorCode.INTER_SERVER_ERROR, request.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}


