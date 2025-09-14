package com.edukit.common.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Component
public class AllowedOriginValidator {

    private final CorsConfigurationSource corsConfigurationSource;

    public AllowedOriginValidator(final CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    public String resolveAllowedOriginHeader(final HttpServletRequest request, final String origin) {
        if (origin == null) {
            return null;
        }
        CorsConfiguration config = corsConfigurationSource.getCorsConfiguration(request);
        if (config == null) {
            return null;
        }
        return config.checkOrigin(origin);
    }

    public boolean allowCredentials(final HttpServletRequest request) {
        CorsConfiguration config = corsConfigurationSource.getCorsConfiguration(request);
        return config != null && Boolean.TRUE.equals(config.getAllowCredentials());
    }
}
