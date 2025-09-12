package com.edukit.core.common.service;

import java.time.Duration;

public interface RedisStoreService {

    void store(String key, String value, Duration ttl);

    String get(String key);

    void delete(String key);

    Long increment(String key, Duration ttl);

    void storeHash(String key, String field, String value, Duration ttl);

    String getHashValue(String key, String field);

    void deleteHash(String key);
}
