package com.edukit.core.studentrecord.metric;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecordGenerationTracker {

    @Value("${record.generation.ttl}")
    private long ttlSeconds;

    private static final String KEY_PREFIX = "sr:gen:";
    private static final String LUA_SCRIPT =
            "local ttl = tonumber(ARGV[1]) or 0 " +
            "local c = redis.call('INCR', KEYS[1]) " +
            "if c == 1 and ttl > 0 then " +
            "  redis.call('EXPIRE', KEYS[1], ttl) " +
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
                String.valueOf(ttlSeconds)
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
