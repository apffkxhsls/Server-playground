package org.example.domain.post.domain.exception;

import org.example.global.exception.BaseException;
import org.example.domain.post.domain.code.PostErrorCode;

public class PostNotFoundException extends BaseException {
    public PostNotFoundException() {
        super(PostErrorCode.POST_NOT_FOUND);
    }
}