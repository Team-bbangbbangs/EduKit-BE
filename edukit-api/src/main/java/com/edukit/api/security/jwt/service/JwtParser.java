package com.edukit.api.security.jwt.service;

import com.edukit.api.security.jwt.provider.JwtKeyProvider;
import com.edukit.api.security.jwt.setting.JwtProperties;
import com.edukit.core.auth.exception.AuthErrorCode;
import com.edukit.core.auth.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtParser {

    private final JwtProperties jwtProperties;

    private static final String BEARER = "Bearer ";

    public Claims parseClaims(final String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, e);
        }
    }

    public String resolveToken(final String token) {
        if (token != null && token.startsWith(BEARER)) {
            return token.substring(BEARER.length());
        }
        throw new AuthException(AuthErrorCode.TOKEN_MISSING);
    }

    public String getMemberUuidFromToken(final String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        return claims.getSubject();
    }

    private Key getSigningKey() {
        return JwtKeyProvider.getSigningKey(jwtProperties.secretKey());
    }
}
