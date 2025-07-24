package com.edukit.external.ai.exception;

import com.edukit.common.exception.ExternalApiException;

public class OpenAiException extends ExternalApiException {

    public OpenAiException(final OpenAiErrorCode errorCode) {
        super(errorCode);
    }

    public OpenAiException(final OpenAiErrorCode errorCode, final Throwable cause) {
        super(errorCode, cause);
    }
}
