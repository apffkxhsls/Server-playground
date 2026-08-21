package org.example.global.exception.code;

import org.springframework.http.HttpStatus;

public interface SuccessCode {
    String getCode();
    HttpStatus getHttpStatus();
    String getMessage();
}
