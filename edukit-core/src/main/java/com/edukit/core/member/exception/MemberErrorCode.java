package com.edukit.core.member.exception;

import com.edukit.common.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND("M-40401", "존재하지 않는 회원입니다. 회원가입을 진행해주세요."),
    ;

    private final String code;
    private final String message;
}
