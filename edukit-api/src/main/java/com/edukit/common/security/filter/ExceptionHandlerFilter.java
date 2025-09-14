package com.edukit.common.security.filter;

import com.edukit.common.EdukitResponse;
import com.edukit.common.exception.BusinessException;
import com.edukit.common.exception.ErrorCode;
import com.edukit.common.security.config.CorsConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final CorsConfig corsConfig;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

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
        if (isAllowedOrigin(origin)) {
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }
        
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(EdukitResponse.fail(customCode, message)));
    }
    
    private boolean isAllowedOrigin(final String origin) {
        if (origin == null) {
            return false;
        }
        CorsConfiguration config = corsConfig.corsConfigurationSource().getCorsConfiguration(null);
        return config != null && Objects.requireNonNull(config.getAllowedOriginPatterns()).contains(origin);
    }
}
