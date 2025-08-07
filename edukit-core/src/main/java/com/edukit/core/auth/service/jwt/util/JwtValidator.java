package com.edukit.core.auth.service.jwt.util;

import com.edukit.core.auth.exception.AuthErrorCode;
import com.edukit.core.auth.exception.AuthException;
import com.edukit.core.auth.service.jwt.type.TokenType;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtValidator {

    private final JwtParser jwtParser;

    private static final String TOKEN_TYPE_CLAIM = "tokenType";

    public void validateToken(final String token, final TokenType type) {
        try {
            Claims claims = jwtParser.parseClaims(token);
            validateClaims(claims, type);
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, e);
        }
    }

    private void validateClaims(final Claims claims, final TokenType expectedType) {
        String memberUuid = claims.getSubject();
        String parsedTokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);

        if (!expectedType.getType().equals(parsedTokenType)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        if (memberUuid == null || memberUuid.isBlank()) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
    }
}
