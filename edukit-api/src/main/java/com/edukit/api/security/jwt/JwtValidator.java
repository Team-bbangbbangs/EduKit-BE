package com.edukit.api.security.jwt;

import com.edukit.auth.exception.AuthErrorCode;
import com.edukit.auth.exception.AuthException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
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
