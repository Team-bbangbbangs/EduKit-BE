package com.edukit.core.auth.service;

import com.edukit.core.auth.jwt.setting.JwtProperties;
import com.edukit.external.redis.KeyValueStoreService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenStoreService {

    private final KeyValueStoreService keyValueStoreService;
    private final JwtProperties jwtProperties;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    public void save(final String memberUuid, final String refreshToken) {
        Duration ttl = Duration.ofMillis(jwtProperties.refreshTokenExpiration());
        keyValueStoreService.set(refreshKey(memberUuid), refreshToken, ttl);
    }

    public String get(final String memberUuid) {
        return keyValueStoreService.get(refreshKey(memberUuid));
    }

    public void delete(final String memberUuid) {
        keyValueStoreService.delete(refreshKey(memberUuid));
    }

    private String refreshKey(final String memberUuid) {
        return REFRESH_TOKEN_PREFIX + memberUuid;
    }
}
