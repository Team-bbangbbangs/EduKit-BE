package com.edukit.core.point.exception;

import com.edukit.common.exception.BusinessException;

public class PointException extends BusinessException {

    public PointException(final PointErrorCode errorCode) {
        super(errorCode);
    }
}
