package com.edukit.core.auth.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    TOKEN_MISSING("A-40101", "토큰이 누락되었습니다."),
    INVALID_TOKEN("A-40102", "유효하지 않은 토큰입니다."),
    UNAUTHORIZED_MEMBER("A-40103", "인증되지 않은 사용자입니다."),
    FORBIDDEN_MEMBER("A-40304", "접근 권한이 없는 사용자입니다. 교사 인증을 진행해주세요."),
    INVALID_EMAIL("A-40005", "유효하지 않은 교사 이메일입니다. 교육청 이메일 도메인만 허용됩니다."),
    MEMBER_ALREADY_REGISTERED("A-40906", "이미 등록된 회원입니다."),
    INVALID_PASSWORD_FORMAT("A-40007", "유효하지 않은 비밀번호 양식입니다.");

    private final String code;
    private final String message;
}
