package com.edukit.api.security.filter;

import com.edukit.api.common.EduKitResponse;
import com.edukit.common.exception.BusinessException;
import com.edukit.common.exception.code.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String UNEXPECTED_CUSTOM_ERROR_CODE = "FAIL-500";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (BusinessException e) {
            handleCustomException(response, e);
        } catch (Exception ex) {
            handleException(response, ex);
        }
    }

    private void handleCustomException(final HttpServletResponse response, final Exception e) throws IOException {
        BusinessException ex = (BusinessException) e;
        ErrorCode errorCode = ex.getErrorCode();
        setResponse(response, errorCode.getCode(), errorCode.getMessage());
    }

    private void handleException(final HttpServletResponse response, final Exception e) throws IOException {
        setResponse(response, UNEXPECTED_CUSTOM_ERROR_CODE, e.getMessage());
    }

    private void setResponse(final HttpServletResponse response, final String customCode, final String message)
            throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(EduKitResponse.fail(customCode, message)));
    }
}
