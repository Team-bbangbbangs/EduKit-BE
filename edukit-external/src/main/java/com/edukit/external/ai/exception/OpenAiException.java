package com.edukit.external.ai.exception;

import com.edukit.common.exception.ExternalException;

public class OpenAiException extends ExternalException {

    public OpenAiException(final OpenAiErrorCode errorCode) {
        super(errorCode);
    }
}
