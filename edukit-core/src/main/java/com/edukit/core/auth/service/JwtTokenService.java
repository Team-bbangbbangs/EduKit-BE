package com.edukit.core.auth.service;

import com.edukit.core.auth.jwt.JwtGenerator;
import com.edukit.core.auth.jwt.JwtParser;
import com.edukit.core.auth.jwt.JwtValidator;
import com.edukit.core.auth.jwt.dto.JwtToken;
import com.edukit.core.auth.jwt.type.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtGenerator jwtGenerator;
    private final JwtParser jwtParser;
    private final JwtValidator jwtValidator;

    public JwtToken generateTokens(final String memberUuid) {
        String accessToken = jwtGenerator.generateToken(memberUuid, TokenType.ACCESS);
        String refreshToken = jwtGenerator.generateToken(memberUuid, TokenType.REFRESH);
        return JwtToken.of(accessToken, refreshToken);
    }

    public String parseMemberUuidFromAccessToken(final String accessToken) {
        String token = jwtParser.resolveToken(accessToken);
        jwtValidator.validateToken(token, TokenType.ACCESS);
        return jwtParser.parseClaims(token).getSubject();
    }
}
