package org.example.global.response;

import org.example.global.code.ErrorCode;
import org.example.global.code.SuccessCode;
import org.springframework.http.ResponseEntity;

public record BaseResponse<T>(
        String code,
        boolean success,
        String message,
        T data
) {
    public static <T> ResponseEntity<BaseResponse<T>> success(SuccessCode successCode, T data) {
        return ResponseEntity
                .status(successCode.getHttpStatus())
                .body(new BaseResponse<>(successCode.getCode(), true, successCode.getMessage(), data));
    }

    public static <T> ResponseEntity<BaseResponse<T>> failure(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new BaseResponse<>(errorCode.getCode(), false, errorCode.getMessage(), null));
    }

    // Handler에 있는 message 사용하기 위해서
    public static <T> ResponseEntity<BaseResponse<T>> failure(ErrorCode errorCode, String message) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new BaseResponse<>(errorCode.getCode(), false, message, null));
    }
}
