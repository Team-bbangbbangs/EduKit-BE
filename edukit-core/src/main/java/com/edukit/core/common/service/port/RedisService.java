package com.edukit.core.common.service.port;

import java.time.Duration;

public interface RedisService {

    void store(String key, String value, Duration ttl);

    String get(String key);

    void delete(String key);
}
