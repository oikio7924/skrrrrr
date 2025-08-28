package com.skrrrrr.harudam.common.exception;

import com.skrrrrr.harudam.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ✅ 전역 예외 처리기
 * - 사용자 친화적 메시지는 프론트에서 관리
 * - 백엔드는 errorCode만 전달
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 잘못된 인자 예외 (주로 서비스 로직) */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        // ex.getMessage()를 errorCode로 사용 (프론트에서 매핑)
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ex.getMessage() != null ? ex.getMessage() : "INVALID_ARGUMENT"));
    }

    /** 요청 값 검증 실패 (DTO @Valid, @NotNull 등) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail("VALIDATION_ERROR"));
    }

    /** 인증 관련 오류 (예: 토큰 불일치, 만료) */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Object>> handleSecurity(SecurityException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail("AUTHENTICATION_FAILED"));
    }

    /** 그 외 모든 예외 (서버 내부 오류) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        ex.printStackTrace(); // 서버 로그는 유지
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("INTERNAL_SERVER_ERROR"));
    }
}
