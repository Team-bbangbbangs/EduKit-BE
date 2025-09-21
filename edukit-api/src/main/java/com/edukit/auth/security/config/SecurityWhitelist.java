package com.edukit.auth.security.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecurityWhitelist {

    static final String[] AUTH_WHITELIST = {
            "/api/v1/auth/signup",
            "/api/v1/auth/login",
            "/api/v1/auth/reissue",
            "/api/v2/auth/password",
            "/api/v1/auth/verify-email",
            "/api/v2/auth/find-password",
            "/api/v1/auth/nickname",
            "/actuator/health",
            "/actuator/prometheus",
            "/actuator/metrics",
            "/metrics"
    };

    static final String[] BUSINESS_WHITE_LIST = {
            "/api/v2/notices",
            "/api/v2/notices/{noticeId:\\d+}"
    };

    static final String[] SWAGGER_WHITE_LIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    private static final String[] ALL_WHITELIST;

    static {
        List<String> allWhitelist = new ArrayList<>();
        allWhitelist.addAll(Arrays.asList(AUTH_WHITELIST));
        allWhitelist.addAll(Arrays.asList(BUSINESS_WHITE_LIST));
        allWhitelist.addAll(Arrays.asList(SWAGGER_WHITE_LIST));
        ALL_WHITELIST = allWhitelist.toArray(new String[0]);
    }

    public static String[] getAllWhitelistPaths() {
        return ALL_WHITELIST.clone();
    }
}
