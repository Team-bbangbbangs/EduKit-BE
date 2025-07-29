package com.edukit.external.redis;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile({"local", "test"})
public class InMemoryStoreService implements KeyValueStoreService {

    private final Map<String, String> memory = new HashMap<>();

    @Override
    public void set(String key, String value, Duration ttl) {
        // TTL 무시
        memory.put(key, value);
    }

    @Override
    public String get(String key) {
        return memory.get(key);
    }

    @Override
    public void delete(String key) {
        memory.remove(key);
    }
}
