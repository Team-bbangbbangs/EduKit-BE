package com.edukit.common.security.handler;

import com.edukit.common.EdukitResponse;
import com.edukit.common.security.utils.AllowedOriginValidator;
import com.edukit.core.auth.exception.AuthErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AllowedOriginValidator allowedOriginValidator;

    private static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException {
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
        String allowed = allowedOriginValidator.resolveAllowedOriginHeader(request, origin);
        if (allowed != null) {
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, allowed);
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }

        response.getWriter().write(objectMapper.writeValueAsString(
                EdukitResponse.fail(AuthErrorCode.UNAUTHORIZED_MEMBER.getCode(),
                        AuthErrorCode.UNAUTHORIZED_MEMBER.getMessage()))
        );
    }

    // no extra helpers
}
