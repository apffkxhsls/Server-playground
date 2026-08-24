package org.example.post.presentation.dto.request;

import org.example.global.exception.BaseException;
import org.example.post.domain.code.PostErrorCode;

public record GetPostRequest(int page, int size) {
    public void validate() {
        if (page < 0 || size < 1) {
            throw new BaseException(PostErrorCode.INVALID_PAGINATION);
        }
    }
}
