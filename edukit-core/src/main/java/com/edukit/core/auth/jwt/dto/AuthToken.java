package com.edukit.core.auth.jwt.dto;

public record AuthToken(
        String accessToken,
        String refreshToken
) {
    public static AuthToken of(final String accessToken, final String refreshToken) {
        return new AuthToken(accessToken, refreshToken);
    }
}
