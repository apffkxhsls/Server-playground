package org.example.global.response;

import org.example.global.code.ErrorCode;
import org.example.global.code.SuccessCode;
import org.springframework.http.ResponseEntity;

public record ApiResponse<T>(
        String code,
        boolean success,
        String message,
        T data
) {
    public static <T> ResponseEntity<ApiResponse<T>> success(SuccessCode successCode, T data) {
        return ResponseEntity
                .status(successCode.getHttpStatus())
                .body(new ApiResponse<>(successCode.getCode(), true, successCode.getMessage(), data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> failure(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(errorCode.getCode(), true, errorCode.getMessage(), null));
    }
}
