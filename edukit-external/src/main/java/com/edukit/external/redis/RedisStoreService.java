package com.edukit.external.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile("!local & !test")
public class RedisStoreService implements KeyValueStoreService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void set(final String key, final String value, final Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public String get(final String key) {
        return redisTemplate.opsForValue().get(key);    // null 반환 가능
    }

    @Override
    public void delete(final String key) {
        redisTemplate.delete(key);
    }
}
