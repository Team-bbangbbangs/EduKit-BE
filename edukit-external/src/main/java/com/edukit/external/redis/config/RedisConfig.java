package com.edukit.external.redis.config;

import com.edukit.common.ServerInstanceManager;
import com.edukit.core.studentrecord.service.RedisStreamConsumer;
import com.edukit.core.studentrecord.service.SSEChannelManager;
import com.edukit.external.redis.RedisStreamServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {

    private final String host;
    private final int port;

    public RedisConfig(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port
    ) {
        this.host = host;
        this.port = port;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisStreamConsumer redisStreamConsumer(
            final RedisStreamServiceImpl redisService,
            final ServerInstanceManager serverInstanceManager,
            final SSEChannelManager sseChannelManager,
            final ObjectMapper objectMapper
    ) {
        return new RedisStreamConsumer(redisService, serverInstanceManager, sseChannelManager, objectMapper);
    }
}
