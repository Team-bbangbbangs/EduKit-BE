package com.edukit.core.auth.jwt.dto;

public record Token(
        String accessToken,
        String refreshToken
) {
    public static Token of(final String accessToken, final String refreshToken) {
        return new Token(accessToken, refreshToken);
    }
}
