package com.edukit.core.notice.exception;

import com.edukit.common.exception.BusinessException;
import com.edukit.common.exception.ErrorCode;

public class NoticeException extends BusinessException {
    public NoticeException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
