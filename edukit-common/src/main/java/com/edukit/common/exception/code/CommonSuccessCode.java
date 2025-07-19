package com.edukit.common.exception.code;

import com.edukit.common.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonSuccessCode implements SuccessCode {

    OK(StatusCode.OK.getStatus(), "SUCCESS", "요청이 성공했습니다."),
    CREATED(StatusCode.CREATED.getStatus(), "SUCCESS", "요청이 성공했습니다.");

    private final int status;
    private final String code;
    private final String message;
}
