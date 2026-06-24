package kd.mgproj.core.error.exception;

import kd.mgproj.core.error.ErrorCode;
import lombok.Getter;

/**
 * 비즈니스 로직 처리 과정에서 발생하는 사용자 정의 예외
 *
 * <p>
 * 서비스 계층에서 비즈니스 규칙 위반 상황이 발생할 경우
 * ErrorCode와 함께 예외를 발생시키며,
 * GlobalExceptionHandler에서 일관된 형식의 에러 응답으로 변환
 * </p>
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}


