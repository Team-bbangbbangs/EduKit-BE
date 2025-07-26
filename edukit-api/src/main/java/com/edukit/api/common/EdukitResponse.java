package com.edukit.api.common;

import com.edukit.common.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class EdukitResponse<T> {

    private final String code;

    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    private EdukitResponse(final String code, final String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public static <T> EdukitResponse<T> success() {
        return new EdukitResponse<>("SUCCESS", "요청이 성공했습니다.");
    }

    public static <T> EdukitResponse<T> success(final T data) {
        return new EdukitResponse<>("SUCCESS", "요청이 성공했습니다.", data);
    }

    public static <T> EdukitResponse<T> fail(final ErrorCode errorCode) {
        return new EdukitResponse<>(errorCode.getCode(), errorCode.getMessage());
    }

    public static <T> EdukitResponse<T> fail(final String code, final String message) {
        return new EdukitResponse<>(code, message);
    }

    public static <T> EdukitResponse<T> fail(final String code, final String message, final T data) {
        return new EdukitResponse<>(code, message, data);
    }
}
