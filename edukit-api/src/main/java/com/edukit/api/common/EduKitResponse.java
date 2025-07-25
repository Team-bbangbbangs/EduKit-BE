package com.edukit.api.common;

import com.edukit.common.exception.code.ErrorCode;
import com.edukit.common.exception.code.SuccessCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class EduKitResponse<T> {

    private final String code;

    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    private EduKitResponse(final String code, final String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public static <T> EduKitResponse<T> success(final SuccessCode successCode) {
        return new EduKitResponse<>(successCode.getCode(), successCode.getMessage());
    }

    public static <T> EduKitResponse<T> success(final SuccessCode successCode, final T data) {
        return new EduKitResponse<>(successCode.getCode(), successCode.getMessage(), data);
    }

    public static <T> EduKitResponse<T> fail(final ErrorCode errorCode) {
        return new EduKitResponse<>(errorCode.getCode(), errorCode.getMessage());
    }

    public static <T> EduKitResponse<T> fail(final String code, final String message) {
        return new EduKitResponse<>(code, message);
    }

    public static <T> EduKitResponse<T> fail(final String code, final String message, final T data) {
        return new EduKitResponse<>(code, message, data);
    }
}
