package com.edukit.core.auth.service;

import com.edukit.core.auth.service.jwt.setting.JwtProperties;
import com.edukit.core.common.service.RedisStoreService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(RedisStoreService.class)
public class RefreshTokenStoreService {

    private final RedisStoreService redisStoreService;
    private final JwtProperties jwtProperties;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    public void store(final String memberUuid, final String refreshToken) {
        Duration ttl = Duration.ofMillis(jwtProperties.refreshTokenExpiration());
        redisStoreService.store(refreshKey(memberUuid), refreshToken, ttl);
    }

    public String get(final String memberUuid) {
        return redisStoreService.get(refreshKey(memberUuid));
    }

    public void delete(final String memberUuid) {
        redisStoreService.delete(refreshKey(memberUuid));
    }

    private String refreshKey(final String memberUuid) {
        return REFRESH_TOKEN_PREFIX + memberUuid;
    }
}
