package com.edukit.external.redis;

import com.edukit.core.common.service.RedisStoreService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisStoreServiceImpl implements RedisStoreService {

    private final RedisTemplate<String, String> redisTemplate;

    public void store(final String key, final String value, final Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public String get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean setIfAbsent(final String key, final String value, final Duration ttl) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, ttl);
    }

    public void delete(final String key) {
        redisTemplate.delete(key);
    }

    public Long increment(final String key, final Duration ttl) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, ttl);
        }
        return count;
    }

    public void storeHash(final String key, final String field, final String value, final Duration ttl) {
        redisTemplate.opsForHash().put(key, field, value);
        redisTemplate.expire(key, ttl);
    }

    public String getHashValue(final String key, final String field) {
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    public void deleteHash(final String key) {
        redisTemplate.delete(key);
    }
}
