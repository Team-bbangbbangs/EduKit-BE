package com.edukit.api.common.filter;

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
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MDCLoggingFilter implements Filter {

    private final JwtTokenService jwtTokenService;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws ServletException, IOException {

        final String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);

        final String memberUuid = getMemberUuidFromToken((HttpServletRequest) request);
        MDC.put("userId", memberUuid);

        chain.doFilter(request, response);
        MDC.clear();
    }

    private String getMemberUuidFromToken(final HttpServletRequest request) {
        String requestedToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (requestedToken == null || requestedToken.isEmpty()) {
            return "anonymous";
        }
        return jwtTokenService.parseMemberUuidFromAccessToken(requestedToken);
    }
}

