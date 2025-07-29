package com.edukit.external.redis;

import java.time.Duration;

public interface KeyValueStoreService {
    void set(final String key, final String value, final Duration ttl);

    String get(final String key);

    void delete(final String key);
}
