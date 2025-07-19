package com.edukit.common;

import com.edukit.common.exception.code.ErrorCode;
import com.edukit.common.exception.code.SuccessCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private final String code;

    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ApiResponse<T> success(final SuccessCode successCode) {
        return new ApiResponse<>(successCode.getCode(), successCode.getMessage());
    }

    public static <T> ApiResponse<T> success(final SuccessCode successCode, final T data) {
        return new ApiResponse<>(successCode.getCode(), successCode.getMessage(), data);
    }

    public static <T> ApiResponse<T> fail(final ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage());
    }
}
