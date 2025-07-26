package com.edukit.core.post.exception;

import com.edukit.common.exception.BusinessException;
import com.edukit.common.exception.ErrorCode;

public class PostException extends BusinessException {
    public PostException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
