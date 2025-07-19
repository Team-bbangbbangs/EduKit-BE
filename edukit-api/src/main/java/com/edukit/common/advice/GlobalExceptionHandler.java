package com.edukit.common.advice;

import com.edukit.common.exception.BusinessException;
import com.edukit.common.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(final BusinessException e) {
        log.info("Business exception occurred: {}", e.getMessage(), e);
        return e.getErrorCode().getMessage();
    }

    @ExceptionHandler(ExternalApiException.class)
    public String handleExternalApiException(final ExternalApiException e) {
        log.warn("External API exception occurred: {}", e.getMessage(), e);
        return e.getErrorCode().getMessage();
    }
}
