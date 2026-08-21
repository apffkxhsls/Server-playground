package org.example.post.domain.exception;

import org.example.global.exception.BaseException;

public class PostNotFoundException extends BaseException {
    public PostNotFoundException() {
        super(PostErrorCode.POST_NOT_FOUND);
    }
}