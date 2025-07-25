package com.edukit.api.security.handler;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieHandler {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    public Cookie createRefreshTokenCookie(final String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(14 * 24 * 60 * 60);
        cookie.setAttribute("SameSite", "None");
        return cookie;
    }
}
