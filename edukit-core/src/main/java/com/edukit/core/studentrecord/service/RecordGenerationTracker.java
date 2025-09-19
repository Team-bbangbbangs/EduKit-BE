package com.edukit.core.studentrecord.service;

import java.time.Duration;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecordGenerationTracker {

    private static final String KEY_PREFIX = "sr:gen:";
    private static final Duration DEFAULT_TTL = Duration.ofDays(1);
    private static final String LUA_SCRIPT =
            "local c = redis.call('INCR', KEYS[1]) " +
            "if c == 1 then " +
            "  redis.call('EXPIRE', KEYS[1], tonumber(ARGV[1])) " +
            "end " +
            "return c";

    private static final DefaultRedisScript<Long> INCR_EXPIRE_SCRIPT;

    static {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(LUA_SCRIPT);
        script.setResultType(Long.class);
        INCR_EXPIRE_SCRIPT = script;
    }

    private final StringRedisTemplate redisTemplate;

    public boolean isFirstGeneration(long recordId) {
        String key = getCountKey(recordId);
        Long newCount = redisTemplate.execute(
                INCR_EXPIRE_SCRIPT,
                Collections.singletonList(key),
                String.valueOf(DEFAULT_TTL.getSeconds())
        );

        if (newCount == null) {
            log.warn("Redis script returned null for key: {}", key);
            return false;
        }

        boolean isFirst = newCount == 1L;
        if (isFirst) {
            log.debug("RecordId: {}, Generation count set to 1 (first)", recordId);
        } else {
            log.debug("RecordId: {}, Generation count: {} (regeneration)", recordId, newCount);
        }
        return isFirst;
    }

    private String getCountKey(long recordId) {
        return KEY_PREFIX + recordId + ":count";
    }
}
