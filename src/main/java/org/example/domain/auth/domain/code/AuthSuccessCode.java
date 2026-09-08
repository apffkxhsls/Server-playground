package org.example.domain.auth.domain.code;

import org.example.global.code.SuccessCode;
import org.springframework.http.HttpStatus;

public enum AuthSuccessCode implements SuccessCode {
    LOGIN_SUCCESS("AUTH-200", HttpStatus.OK, "로그인에 성공하였습니다."),
    ME_READ_SUCCESS("AUTH-201", HttpStatus.OK, "내 정보 조회에 성공하였습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    AuthSuccessCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
