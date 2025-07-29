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
    public void set(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);    // null 반환 가능
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
