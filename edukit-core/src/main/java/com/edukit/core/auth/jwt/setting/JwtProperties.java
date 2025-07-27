package com.edukit.core.auth.jwt.setting;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("security.jwt.token")
public record JwtProperties(
        String secretKey,
        long accessTokenExpiration,
        long refreshTokenExpiration
) {
}

