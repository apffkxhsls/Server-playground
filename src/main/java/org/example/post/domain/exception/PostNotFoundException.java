package org.example.post.domain.exception;

import org.example.global.exception.BaseException;
import org.example.post.domain.code.PostErrorCode;

public class PostNotFoundException extends BaseException {
    public PostNotFoundException() {
        super(PostErrorCode.POST_NOT_FOUND);
    }
}