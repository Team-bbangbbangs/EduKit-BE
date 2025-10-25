package com.edukit.core.point.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointErrorCode implements ErrorCode {

    INSUFFICIENT_POINTS("P-40001", "포인트가 부족합니다.");

    private final String code;
    private final String message;
}
