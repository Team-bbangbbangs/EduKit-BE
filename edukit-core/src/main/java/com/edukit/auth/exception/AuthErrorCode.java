package com.edukit.auth.exception;

import com.edukit.common.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    TOKEN_MISSING("AUTH-40101", "토큰이 누락되었습니다."),
    INVALID_TOKEN("AUTH-40102", "유효하지 않은 토큰입니다."),
    ;

    private final String code;
    private final String message;
}
