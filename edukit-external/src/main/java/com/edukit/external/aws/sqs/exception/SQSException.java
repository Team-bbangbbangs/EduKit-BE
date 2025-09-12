package com.edukit.external.aws.sqs.exception;

import com.edukit.common.exception.ExternalException;

public class SQSException extends ExternalException {

    public SQSException(final SQSErrorCode errorCode) {
        super(errorCode);
    }

    public SQSException(final SQSErrorCode errorCode, final Throwable e) {
        super(errorCode, e.getMessage());
    }
}
