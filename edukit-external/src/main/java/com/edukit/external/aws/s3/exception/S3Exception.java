package com.edukit.external.aws.s3.exception;

import com.edukit.common.exception.ErrorCode;
import com.edukit.common.exception.ExternalException;

public class S3Exception extends ExternalException {

    public S3Exception(final ErrorCode errorCode) {
        super(errorCode);
    }
}
