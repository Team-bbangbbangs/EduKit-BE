package com.edukit.common.advice;

import com.edukit.common.EdukitResponse;
import com.edukit.common.exception.BusinessException;
import com.edukit.common.exception.ExternalException;
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
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Custom exceptions
    @ExceptionHandler(BusinessException.class)
    public EdukitResponse<Void> handleBusinessException(final BusinessException e) {
        log.info("Business exception occurred: {}", e.getMessage());
        return EdukitResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(ExternalException.class)
    public EdukitResponse<Void> handleExternalApiException(final ExternalException e) {
        log.warn("External API exception occurred: {}", e.getMessage());
        return EdukitResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public EdukitResponse<Map<String, String>> handleValidationException(final MethodArgumentNotValidException e) {
        log.info("Validation exception occurred: {}", e.getMessage());

        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return EdukitResponse.fail("FAIL-400", "validation 오류", validationErrors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public EdukitResponse<Map<String, String>> handleHandlerMethodValidationException(
            final HandlerMethodValidationException e) {
        log.info("Validation parameter exception occurred: {}", e.getMessage());

        Map<String, String> validationErrors = new HashMap<>();
        for (var error : e.getAllErrors()) {
            String fieldName;
            if (error instanceof FieldError fe) {
                fieldName = fe.getField();
            } else {
                String[] codes = error.getCodes();
                if (codes.length > 0) {
                    String firstCode = codes[0];
                    int dotIndex = firstCode.lastIndexOf('.');
                    fieldName = dotIndex != -1 ? firstCode.substring(dotIndex + 1) : firstCode;
                } else {
                    fieldName = "unknown";
                }
            }
            validationErrors.put(fieldName, error.getDefaultMessage());
        }

        return EdukitResponse.fail("FAIL-400", "validation 오류", validationErrors);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public EdukitResponse<Void> handleMissingCookieException(final MissingRequestCookieException e) {
        log.info("Missing request cookie exception occurred: {}", e.getMessage());
        return EdukitResponse.fail("FAIL-401", "필수 쿠키가 누락되었습니다.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public EdukitResponse<Void> handleMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        log.warn("Method not supported exception occurred: {}", e.getMessage());
        return EdukitResponse.fail("FAIL-405", "지원하지 않는 HTTP 메소드입니다.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public EdukitResponse<Void> handleMediaTypeNotSupportedException(final HttpMediaTypeNotSupportedException e) {
        log.warn("Media type not supported exception occurred: {}", e.getMessage());
        return EdukitResponse.fail("FAIL-415", "지원하지 않는 미디어 타입입니다.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public EdukitResponse<Void> handleMissingParameterException(final MissingServletRequestParameterException e) {
        log.warn("Missing request parameter exception occurred: {}", e.getMessage());
        return EdukitResponse.fail("FAIL-400", "필수 요청 파라미터가 누락되었습니다.");
    }

    @ExceptionHandler(TypeMismatchException.class)
    public EdukitResponse<Void> handleTypeMismatchException(final TypeMismatchException e) {
        log.warn("Type mismatch exception occurred: {}", e.getMessage());
        return EdukitResponse.fail("FAIL-400", "요청 파라미터 타입이 일치하지 않습니다.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public EdukitResponse<Void> handleMessageNotReadableException(final HttpMessageNotReadableException e) {
        log.warn("Message not readable exception occurred: {}", e.getMessage());
        return EdukitResponse.fail("FAIL-400", "요청 본문이 올바르지 않습니다.");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public EdukitResponse<Void> handleNoHandlerFoundException(final NoHandlerFoundException e) {
        log.warn("No handler found exception occurred: {} {}", e.getHttpMethod(), e.getRequestURL());
        return EdukitResponse.fail("FAIL-404", "존재하지 않는 요청 경로입니다.");
    }

    // Generic exception handler
    @ExceptionHandler(Exception.class)
    public EdukitResponse<Void> handleGenericException(Exception e) {
        Throwable cause = getDeepCause(e);
        log.error("Unexpected exception occurred. Original: [{}], Root cause: [{}]", e.getMessage(), cause.getMessage(),
                e);
        return EdukitResponse.fail("FAIL-500", "서버 내부 오류가 발생했습니다.");
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
