package com.edukit.core.member.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND("M-40401", "존재하지 않는 회원입니다. 회원가입을 진행해주세요."),
    MEMBER_ALREADY_REGISTERED("M-40902", "이미 등록된 회원입니다."),
    INVALID_SCHOOL_TYPE("M-40003", "중학교, 고등학교 중 하나를 선택해주세요."),
    INVALID_NICKNAME("EDMT-40004", "입력하신 닉네임은 유효하지 않습니다."),
    DUPLICATED_NICKNAME("EDMT-40005", "입력하신 닉네임은 중복된 닉네임입니다."),
    ;

    private final String code;
    private final String message;
}
