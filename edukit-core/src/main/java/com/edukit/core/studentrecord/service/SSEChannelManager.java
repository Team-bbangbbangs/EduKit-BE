package com.edukit.core.studentrecord.service;


import com.edukit.common.ServerInstanceManager;
import com.edukit.core.common.service.RedisService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(RedisService.class)
public class SSEChannelManager {

    private final RedisService redisService;
    private final ServerInstanceManager serverInstanceManager;

    private static final String SSE_CHANNEL_PREFIX = "sse-channel:";

    public void storeChannel(final String taskId) {
        String serverId = serverInstanceManager.getServerId();
        redisService.store(sseChannelKey(taskId), serverId, Duration.ofHours(1));
    }

    public String get(final String channelId) {
        return redisService.get(sseChannelKey(channelId));
    }

    public boolean hasActivateChannel(final String channelId) {
        return redisService.get(sseChannelKey(channelId)) != null;
    }

    public void deleteChannel(final String channelId) {
        redisService.delete(sseChannelKey(channelId));
    }

    private String sseChannelKey(final String channelId) {
        return SSE_CHANNEL_PREFIX + channelId;
    }
}
