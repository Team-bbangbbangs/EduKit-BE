package com.edukit.core.studentrecord.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecordGenerationTracker {

    private static final String KEY_PREFIX = "sr:gen:";
    private static final Duration DEFAULT_TTL = Duration.ofDays(1);

    private final StringRedisTemplate redisTemplate;

    public boolean isFirstGeneration(long recordId) {
        String key = getCountKey(recordId);
        Long newCount = redisTemplate.opsForValue().increment(key);

        if (newCount == null) {
            log.warn("Redis INCR returned null for key: {}", key);
            return false;
        }

        if (newCount == 1L) {
            // 최초 생성 시에만 TTL 설정 (비정상 흐름 방치 대비)
            redisTemplate.expire(key, DEFAULT_TTL);
            log.debug("RecordId: {}, Generation count set to 1 (first)", recordId);
            return true;
        }

        log.debug("RecordId: {}, Generation count: {} (regeneration)", recordId, newCount);
        return false;
    }

    private String getCountKey(long recordId) {
        return KEY_PREFIX + recordId + ":count";
    }
}
