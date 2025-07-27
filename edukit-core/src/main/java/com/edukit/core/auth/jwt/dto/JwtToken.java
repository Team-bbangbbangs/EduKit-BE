package com.edukit.core.auth.jwt.dto;

public record JwtToken(
        String accessToken,
        String refreshToken
) {
    public static JwtToken of(final String accessToken, final String refreshToken) {
        return new JwtToken(accessToken, refreshToken);
    }
}
