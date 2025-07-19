package com.edukit.api.common.response;

import com.edukit.common.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // ApiResponse 타입이거나 ResponseEntity<ApiResponse> 타입인 경우 처리
        Class<?> parameterType = returnType.getParameterType();
        return ApiResponse.class.isAssignableFrom(parameterType) ||
               (ResponseEntity.class.isAssignableFrom(parameterType) && 
                returnType.getGenericParameterType().toString().contains("ApiResponse"));
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, ServerHttpResponse response) {
        
        // 이미 ResponseEntity로 감싸져 있다면 그대로 반환
        if (body instanceof ResponseEntity) {
            return body;
        }
        
        // ApiResponse인 경우 적절한 HTTP 상태코드로 변환
        if (body instanceof ApiResponse<?> apiResponse) {
            HttpStatus httpStatus = mapToHttpStatus(apiResponse.getCode());
            response.setStatusCode(httpStatus);
            return apiResponse;
        }
        
        return body;
    }

    /**
     * ApiResponse의 code를 HTTP 상태코드로 매핑
     */
    private HttpStatus mapToHttpStatus(String code) {
        // 성공 코드 매핑
        if (isSuccessCode(code)) {
            return switch (code) {
                case "CREATED", "201" -> HttpStatus.CREATED;
                case "ACCEPTED", "202" -> HttpStatus.ACCEPTED; 
                case "NO_CONTENT", "204" -> HttpStatus.NO_CONTENT;
                default -> HttpStatus.OK;
            };
        }
        
        // 에러 코드 매핑
        return switch (code) {
            case "INVALID_INPUT", "VALIDATION_FAILED", "400" -> HttpStatus.BAD_REQUEST;
            case "UNAUTHORIZED", "401" -> HttpStatus.UNAUTHORIZED;
            case "ACCESS_DENIED", "FORBIDDEN", "403" -> HttpStatus.FORBIDDEN;
            case "NOT_FOUND", "404" -> HttpStatus.NOT_FOUND;
            case "METHOD_NOT_ALLOWED", "405" -> HttpStatus.METHOD_NOT_ALLOWED;
            case "CONFLICT", "409" -> HttpStatus.CONFLICT;
            case "UNPROCESSABLE_ENTITY", "422" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "EXTERNAL_API_ERROR", "502" -> HttpStatus.BAD_GATEWAY;
            case "SERVICE_UNAVAILABLE", "503" -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    /**
     * 성공 코드인지 판별 (200번대이거나 일반적인 성공 코드)
     */
    private boolean isSuccessCode(String code) {
        if (code.matches("^2\\d{2}$")) { // 200-299
            return true;
        }
        
        return switch (code) {
            case "SUCCESS", "CREATED", "UPDATED", "DELETED", "ACCEPTED", "NO_CONTENT" -> true;
            default -> false;
        };
    }
} 