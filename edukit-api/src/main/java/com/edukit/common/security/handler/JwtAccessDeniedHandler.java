package com.edukit.common.security.handler;

import com.edukit.common.EdukitResponse;
import com.edukit.common.security.config.CorsConfig;
import com.edukit.core.auth.exception.AuthErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final CorsConfig corsConfig;

    private static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response,
                       final AccessDeniedException accessDeniedException) throws IOException {
        handleException(request, response);
    }

    private void handleException(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        setResponse(request, response);
    }

    private void setResponse(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // CORS 헤더 설정 (허용된 origin만)
        String origin = request.getHeader("Origin");
        if (isAllowedOrigin(origin)) {
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }

        response.getWriter().write(objectMapper.writeValueAsString(
                EdukitResponse.fail(AuthErrorCode.FORBIDDEN_MEMBER.getCode(),
                        AuthErrorCode.FORBIDDEN_MEMBER.getMessage()))
        );
    }

    private boolean isAllowedOrigin(final String origin) {
        if (origin == null) {
            return false;
        }
        CorsConfiguration config = corsConfig.corsConfigurationSource().getCorsConfiguration(null);
        return config != null && Objects.requireNonNull(config.getAllowedOriginPatterns()).contains(origin);
    }
}
