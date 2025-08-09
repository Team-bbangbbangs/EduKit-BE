package com.edukit.core.auth.service;

import com.edukit.core.auth.service.jwt.setting.JwtProperties;
import com.edukit.core.common.port.RedisService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(RedisService.class)
public class RefreshTokenStoreService {

    private final RedisService redisService;
    private final JwtProperties jwtProperties;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    public void store(final String memberUuid, final String refreshToken) {
        Duration ttl = Duration.ofMillis(jwtProperties.refreshTokenExpiration());
        redisService.store(refreshKey(memberUuid), refreshToken, ttl);
    }

    public String get(final String memberUuid) {
        return redisService.get(refreshKey(memberUuid));
    }

    public void delete(final String memberUuid) {
        redisService.delete(refreshKey(memberUuid));
    }

    private String refreshKey(final String memberUuid) {
        return REFRESH_TOKEN_PREFIX + memberUuid;
    }
}
