package com.edukit.external.aws.sqs.exception;

import com.edukit.common.exception.ExternalApiException;

public class SQSException extends ExternalApiException {

    public SQSException(final SQSErrorCode errorCode) {
        super(errorCode);
    }

    public SQSException(final SQSErrorCode errorCode, final Throwable e) {
        super(errorCode, e.getMessage());
    }
}
