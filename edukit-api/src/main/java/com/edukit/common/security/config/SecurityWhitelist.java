package com.edukit.common.security.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecurityWhitelist {

    static final String[] AUTH_WHITELIST = {
            "/api/v1/auth/signup",
            "/api/v1/auth/login",
            "/api/v1/auth/reissue",
            "/api/v1/auth/password",
            "/api/v1/auth/verify-email",
            "/api/v1/auth/find-password",
            "/actuator/health",
            "/actuator/prometheus"
    };

    static final String[] BUSINESS_WHITE_LIST = {
            "/api/v1/notices",
            "/api/v1/notices/{noticeId:\\d+}"
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
