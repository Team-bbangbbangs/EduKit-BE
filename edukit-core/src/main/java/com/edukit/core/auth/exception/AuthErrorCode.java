package com.edukit.core.auth.exception;

import com.edukit.common.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    TOKEN_MISSING("AUTH-40101", "토큰이 누락되었습니다."),
    INVALID_TOKEN("AUTH-40102", "유효하지 않은 토큰입니다."),
    UNAUTHORIZED_MEMBER("AUTH-40103", "인증되지 않은 사용자입니다."),
    FORBIDDEN_MEMBER("AUTH-40304", "접근 권한이 없는 사용자입니다."),
    ;

    private final String code;
    private final String message;
}
