package com.edukit.external.redis.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisErrorCode implements ErrorCode {
    MESSAGE_PROCESSING_FAILED("R-50001", "Redis 메시지 처리 중 오류가 발생했습니다.");

    private final String code;
    private final String message;
}
