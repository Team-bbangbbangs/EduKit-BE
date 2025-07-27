package com.edukit.external.s3.exception;

import com.edukit.common.exception.ExternalApiException;
import com.edukit.common.exception.code.ErrorCode;

public class S3Exception extends ExternalApiException {
    public S3Exception(final ErrorCode errorCode) {
        super(errorCode);
    }

    public S3Exception(final ErrorCode errorCode, final Throwable cause) {
        super(errorCode, cause);
    }
}
