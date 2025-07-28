package com.edukit.api.security.handler;

import com.edukit.core.auth.jwt.setting.JwtProperties;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class RefreshTokenCookieHandler {

    private final JwtProperties jwtProperties;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    public ResponseCookie createRefreshTokenCookie(final String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofMillis(jwtProperties.refreshTokenExpiration()))
                .build();
    }

    public ResponseCookie createClearedRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(0)
                .build();
    }
}
