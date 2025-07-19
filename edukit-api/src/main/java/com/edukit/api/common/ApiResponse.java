package com.edukit.api.common;

import com.edukit.common.exception.code.ErrorCode;
import com.edukit.common.exception.code.SuccessCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    @JsonIgnore
    private final int status;

    private final String code;

    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ApiResponse<T> success(final SuccessCode successCode) {
        return new ApiResponse<>(successCode.getStatus(), successCode.getCode(), successCode.getMessage());
    }

    public static <T> ApiResponse<T> success(final SuccessCode successCode, final T data) {
        return new ApiResponse<>(successCode.getStatus(), successCode.getCode(), successCode.getMessage(), data);
    }

    public static <T> ApiResponse<T> fail(final ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
    }

    public static <T> ApiResponse<T> fail(final int status, final String code, final String message) {
        return new ApiResponse<>(status, code, message);
    }

    public static <T> ApiResponse<T> fail(final int status, final String code, final String message, final T data) {
        return new ApiResponse<>(status, code, message, data);
    }
}
