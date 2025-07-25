package com.edukit.external.s3.dto.exception;

import com.edukit.common.exception.ExternalApiException;
import com.edukit.common.exception.code.ErrorCode;

public class S3Exception extends ExternalApiException {
    public S3Exception(ErrorCode errorCode) {
        super(errorCode);
    }

    public S3Exception(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
