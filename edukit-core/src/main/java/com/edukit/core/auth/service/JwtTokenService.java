package com.edukit.core.auth.service;

import com.edukit.core.auth.service.jwt.JwtGenerator;
import com.edukit.core.auth.service.jwt.JwtParser;
import com.edukit.core.auth.service.jwt.JwtValidator;
import com.edukit.core.auth.service.jwt.dto.AuthToken;
import com.edukit.core.auth.service.jwt.type.TokenType;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtGenerator jwtGenerator;
    private final JwtParser jwtParser;
    private final JwtValidator jwtValidator;

    public AuthToken generateTokens(final String memberUuid) {
        String accessToken = jwtGenerator.generateToken(memberUuid, TokenType.ACCESS);
        String refreshToken = jwtGenerator.generateToken(memberUuid, TokenType.REFRESH);
        return AuthToken.of(accessToken, refreshToken);
    }

    public String parseMemberUuidFromAccessToken(final String accessToken) {
        String token = jwtParser.resolveToken(accessToken);
        jwtValidator.validateToken(token, TokenType.ACCESS);
        return jwtParser.parseClaims(token).getSubject();
    }

    public String parseMemberUuidFromRefreshToken(final String refreshToken) {
        jwtValidator.validateToken(refreshToken, TokenType.REFRESH);
        return jwtParser.parseClaims(refreshToken).getSubject();
    }

    public boolean isTokenEqual(final String requestToken, final String storedToken) {
        if (requestToken == null || storedToken == null) {
            return false;
        }

        return MessageDigest.isEqual(
                storedToken.getBytes(StandardCharsets.UTF_8),
                requestToken.getBytes(StandardCharsets.UTF_8)
        );
    }
}
