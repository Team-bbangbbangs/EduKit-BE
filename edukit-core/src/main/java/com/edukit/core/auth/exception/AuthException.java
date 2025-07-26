package com.edukit.core.auth.exception;

import com.edukit.common.exception.BusinessException;
import com.edukit.common.exception.ErrorCode;

public class AuthException extends BusinessException {

    public AuthException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(final ErrorCode errorCode, final Throwable cause) {
        super(errorCode, cause);
    }
}
