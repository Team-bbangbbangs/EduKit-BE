package com.edukit.auth.security.filter;

import com.edukit.common.EdukitResponse;
import com.edukit.common.exception.BusinessException;
import com.edukit.common.exception.ErrorCode;
import com.edukit.auth.security.utils.AllowedOriginValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final AllowedOriginValidator allowedOriginValidator;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String HEADER_VARY = "Vary";
    private static final String UNEXPECTED_CUSTOM_ERROR_CODE = "FAIL-500";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (BusinessException e) {
            handleCustomException(request, response, e);
        } catch (Exception ex) {
            handleException(request, response, ex);
        }
    }

    private void handleCustomException(final HttpServletRequest request, final HttpServletResponse response,
                                       final Exception e) throws IOException {
        BusinessException ex = (BusinessException) e;
        ErrorCode errorCode = ex.getErrorCode();
        setResponse(request, response, errorCode.getCode(), errorCode.getMessage());
    }

    private void handleException(final HttpServletRequest request, final HttpServletResponse response,
                                 final Exception e) throws IOException {
        setResponse(request, response, UNEXPECTED_CUSTOM_ERROR_CODE, e.getMessage());
    }

    private void setResponse(final HttpServletRequest request, final HttpServletResponse response,
                             final String customCode, final String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        
        // CORS 헤더 설정 (허용된 origin만)
        String origin = request.getHeader("Origin");
        String allowed = allowedOriginValidator.resolveAllowedOriginHeader(request, origin);
        if (allowed != null) {
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, allowed);
            if (allowedOriginValidator.allowCredentials(request)) {
                response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            }
            response.addHeader(HEADER_VARY, "Origin");
        }
        
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(EdukitResponse.fail(customCode, message)));
    }
    
    // no extra helpers
}
