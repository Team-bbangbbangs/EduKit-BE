package com.edukit.api.common.advice;

import com.edukit.api.common.ApiResponse;
import com.edukit.common.exception.BusinessException;
import com.edukit.common.exception.ExternalApiException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String FAIL = "FAIL";

    // Custom exceptions
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(final BusinessException e) {
        log.info("Business exception occurred: {}", e.getMessage(), e);
        return ApiResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(ExternalApiException.class)
    public ApiResponse<Void> handleExternalApiException(final ExternalApiException e) {
        log.warn("External API exception occurred: {}", e.getMessage(), e);
        return ApiResponse.fail(e.getErrorCode());
    }

    // Spring MVC exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Map<String, String>> handleValidationException(final MethodArgumentNotValidException e) {
        log.info("Validation exception occurred: {}", e.getMessage());

        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ApiResponse.fail(FAIL, "validation 오류", validationErrors);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ApiResponse<Void> handleMissingCookieException(final MissingRequestCookieException e) {
        log.info("Missing request cookie exception occurred: {}", e.getMessage());
        return ApiResponse.fail(FAIL, "필수 쿠키가 누락되었습니다.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResponse<Void> handleMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        log.warn("Method not supported exception occurred: {}", e.getMessage());
        return ApiResponse.fail(FAIL, "지원하지 않는 HTTP 메소드입니다.");
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ApiResponse<Void> handleMissingPathVariableException(final MissingPathVariableException e) {
        log.warn("Missing path variable exception occurred: {}", e.getMessage());
        return ApiResponse.fail(FAIL, "필수 경로 변수 누락입니다.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ApiResponse<Void> handleMediaTypeNotSupportedException(final HttpMediaTypeNotSupportedException e) {
        log.warn("Media type not supported exception occurred: {}", e.getMessage());
        return ApiResponse.fail(FAIL, "지원하지 않는 미디어 타입입니다.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResponse<Void> handleMissingParameterException(final MissingServletRequestParameterException e) {
        log.warn("Missing request parameter exception occurred: {}", e.getMessage());
        return ApiResponse.fail(FAIL, "필수 요청 파라미터가 누락되었습니다.");
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ApiResponse<Void> handleTypeMismatchException(final TypeMismatchException e) {
        log.warn("Type mismatch exception occurred: {}", e.getMessage());
        return ApiResponse.fail(FAIL, "요청 파라미터 타입이 일치하지 않습니다.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleMessageNotReadableException(final HttpMessageNotReadableException e) {
        log.warn("Message not readable exception occurred: {}", e.getMessage());
        return ApiResponse.fail(FAIL, "요청 본문이 올바르지 않습니다.");
    }

    // Generic exception handler
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleGenericException(Exception e) {
        Throwable cause = getDeepCause(e);
        log.error("Unexpected exception occurred. Original: [{}], Root cause: [{}]", e.getMessage(), cause.getMessage(), e);
        return ApiResponse.fail(FAIL, "서버 내부 오류가 발생했습니다.");
    }

    private Throwable getDeepCause(Throwable e) {
        Set<Throwable> visited = new HashSet<>();
        while (e.getCause() != null) {
            if (!visited.add(e)) {
                break; // 순환 참조 감지
            }
            e = e.getCause();
        }
        return e;
    }
}
