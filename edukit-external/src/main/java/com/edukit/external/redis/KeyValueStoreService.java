package com.edukit.external.redis;

import java.time.Duration;

public interface KeyValueStoreService {
    void set(String key, String value, Duration ttl);

    String get(String key);

    void delete(String key);
}
