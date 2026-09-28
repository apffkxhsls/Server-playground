package org.example.domain.post.domain.code;

import org.example.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PostErrorCode implements ErrorCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "PST-001", "존재하지 않는 게시글입니다."),
    INVALID_POST_TITLE(HttpStatus.BAD_REQUEST, "PST-002", "게시글 제목은 필수입니다."),
    INVALID_BOARD_TYPE(HttpStatus.BAD_REQUEST, "PST-003", "게시판 종류는 필수입니다."),
    INVALID_PAGINATION(HttpStatus.BAD_REQUEST, "PST-004", "페이지 요청 값이 올바르지 않습니다."),
    INVALID_POST_USER(HttpStatus.BAD_REQUEST, "PST-005", "게시글 작성자는 필수입니다."),
    POST_FORBIDDEN(HttpStatus.FORBIDDEN, "PST-006", "게시글에 대한 권한이 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    PostErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}