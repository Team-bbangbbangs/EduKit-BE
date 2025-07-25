package com.edukit.api.security.config;

import java.util.List;
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

    public static List<String> getAllWhitelistPaths() {
        return ALL_WHITELIST;
    }

    public static String[] getBusinessWhitelistArray() {
        return BUSINESS_WHITE_LIST.toArray(new String[0]);
    }

    public static String[] getAuthWhitelistArray() {
        return AUTH_WHITELIST.toArray(new String[0]);
    }
}
