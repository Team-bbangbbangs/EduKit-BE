package com.edukit.common.exception;

import com.edukit.common.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class ExternalApiException extends RuntimeException {

    private final ErrorCode errorCode;

    public ExternalApiException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
