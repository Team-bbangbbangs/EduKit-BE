package com.edukit.api.security.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SecurityWhitelist {

    protected static final List<String> AUTH_WHITELIST = List.of(
            "/api/v1/auth/signup",
            "/api/v1/auth/login",
            "/api/v1/auth/reissue",
            "/api/v1/auth/password",
            "/api/v1/auth/verify-email",
            "/api/v1/auth/find-password",
            "/actuator/health",
            "/actuator/prometheus"
    );

    protected static final List<String> BUSINESS_WHITE_LIST = List.of(
            "/api/v1/notices",
            "/api/v1/notices/{noticeId:\\d+}"
    );

    private static final List<String> ALL_WHITELIST;

    static {
        ALL_WHITELIST = Stream.concat(
                AUTH_WHITELIST.stream(),
                BUSINESS_WHITE_LIST.stream()
        ).toList();
    }

    public static List<String> getWhitelistPaths() {
        return ALL_WHITELIST;
    }
}
