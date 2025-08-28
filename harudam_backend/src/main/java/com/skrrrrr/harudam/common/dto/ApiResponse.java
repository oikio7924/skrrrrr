package com.skrrrrr.harudam.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ✅ 모든 API에서 공통으로 사용할 응답 Wrapper
 * - success: 성공 여부
 * - code: 성공/실패 식별 코드 (프론트에서 메시지 매핑용)
 * - data: 실제 데이터 (없으면 null)
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final boolean success;
    private final String code;
    private final T data;

    /** 성공 (기본 코드: OK) */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", data);
    }

    /** 성공 (커스텀 코드) */
    public static <T> ApiResponse<T> ok(String code, T data) {
        return new ApiResponse<>(true, code, data);
    }

    /** 실패 (에러 코드) */
    public static <T> ApiResponse<T> fail(String code) {
        return new ApiResponse<>(false, code, null);
    }
}
