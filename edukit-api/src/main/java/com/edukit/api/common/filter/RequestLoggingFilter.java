package com.edukit.api.common.filter;

import com.edukit.core.auth.service.JwtTokenService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RequestLoggingFilter implements Filter {

    private final JwtTokenService tokenService;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            MDC.put("requestUrl", httpRequest.getRequestURI());
            MDC.put("method", httpRequest.getMethod());

            final String traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);

            String userId = extractUserIdFromRequest(httpRequest);
            MDC.put("userId", userId);

            // Request
            MDC.put("requestType", "request");
            log.info("Received request: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());

            long startTime = System.currentTimeMillis();

            chain.doFilter(request, response);

            // Response
            long duration = System.currentTimeMillis() - startTime;
            MDC.put("responseType", "response");
            MDC.put("status", String.valueOf(httpResponse.getStatus()));
            MDC.put("duration", String.valueOf(duration));

            log.info("Response: {} {} - Status: {}, Duration: {}ms",
                    httpRequest.getMethod(), httpRequest.getRequestURI(),
                    httpResponse.getStatus(), duration);

        } finally {
            MDC.clear();
        }
    }

    private String extractUserIdFromRequest(final HttpServletRequest request) {
        String token = resolveToken(request);
        if (token != null && !token.isBlank()) {
            return tokenService.parseMemberUuidFromAccessToken(token);
        }
        return "anonymous";
    }

    private String resolveToken(final HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
}
