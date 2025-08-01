package com.edukit.external.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisStoreService {

    private final RedisTemplate<String, String> redisTemplate;

    public void store(final String key, final String value, final Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public String get(final String key) {
        return redisTemplate.opsForValue().get(key);    // null 반환 가능
    }

    public void delete(final String key) {
        redisTemplate.delete(key);
    }
}
