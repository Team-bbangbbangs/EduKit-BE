package com.edukit.external.aws.s3.exception;

import com.edukit.common.exception.ErrorCode;
import com.edukit.common.exception.ExternalApiException;

public class S3Exception extends ExternalApiException {
    public S3Exception(final ErrorCode errorCode) {
        super(errorCode);
    }

    public S3Exception(final ErrorCode errorCode, final Throwable cause) {
        super(errorCode, cause);
    }
}
