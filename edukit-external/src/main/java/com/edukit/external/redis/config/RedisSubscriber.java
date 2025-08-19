package com.edukit.external.redis.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriber {

    private final RedisMessageListenerContainer container;
    private final MessageListenerAdapter listenerAdapter;

    private static final String REDIS_CHANNEL = "ai-response";

    @PostConstruct
    public void init() {
        container.addMessageListener(listenerAdapter, new ChannelTopic(REDIS_CHANNEL));
        log.info("Subscribed to Redis channel: ai-response");
    }
}
