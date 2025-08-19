package com.edukit.external.redis.exception;

import com.edukit.common.exception.ExternalException;

public class RedisException extends ExternalException {

    public RedisException(final RedisErrorCode errorCode) {
        super(errorCode);
    }
}
