package com.edukit.common.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonSuccessCode implements SuccessCode {

    OK("SUCCESS", "요청이 성공했습니다."),
    CREATED("SUCCESS", "요청이 성공했습니다.");

    private final String code;
    private final String message;
}
