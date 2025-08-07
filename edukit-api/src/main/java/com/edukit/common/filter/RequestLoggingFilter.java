package com.edukit.common.filter;

import com.edukit.core.auth.service.JwtTokenService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
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

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

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
            MDC.put("requestType", "response");
            MDC.put("duration", String.valueOf(duration));

            log.info("Response: {} {} - Duration: {}ms",
                    httpRequest.getMethod(), httpRequest.getRequestURI(), duration);

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
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
