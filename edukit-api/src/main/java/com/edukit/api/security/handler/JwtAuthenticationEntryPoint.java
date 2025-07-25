package com.edukit.api.security.handler;

import com.edukit.api.common.ApiResponse;
import com.edukit.core.auth.exception.AuthErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException {
        handleException(response);
    }

    private void handleException(final HttpServletResponse response) throws IOException {
        setResponse(response);
    }

    private void setResponse(final HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(
                ApiResponse.fail(AuthErrorCode.UNAUTHORIZED_MEMBER.getCode(), AuthErrorCode.UNAUTHORIZED_MEMBER.getMessage())));
    }
}
