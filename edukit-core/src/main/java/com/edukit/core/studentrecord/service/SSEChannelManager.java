package com.edukit.core.studentrecord.service;

import com.edukit.common.ServerInstanceManager;
import com.edukit.core.common.event.ai.dto.AIResponseMessage;
import com.edukit.core.common.service.RedisStoreService;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBean(RedisStoreService.class)
public class SSEChannelManager {

    private final RedisStoreService redisStoreService;
    private final ServerInstanceManager serverInstanceManager;
    private final AITaskService aiTaskService;
    private final ConcurrentHashMap<String, SseEmitter> activeChannels = new ConcurrentHashMap<>();

    private static final String SSE_CHANNEL_PREFIX = "sse-channel:";
    private static final String RESPONSE_COUNT_PREFIX = "response-count:";
    private static final String REDIS_CHANNEL = "ai-response";
    private static final int MAX_RESPONSE_COUNT = 3;
    private static final Duration RESPONSE_COUNT_TTL = Duration.ofMinutes(5);

    public void registerTaskChannel(final String taskId, final SseEmitter emitter) {
        String serverId = serverInstanceManager.getServerId();
        redisStoreService.store(sseChannelKey(taskId), serverId, Duration.ofHours(1));
        activeChannels.put(taskId, emitter);
        log.info("Registered SSE channel for taskId: {} on server: {}", taskId, serverId);
    }

    public String get(final String taskId) {
        return redisStoreService.get(sseChannelKey(taskId));
    }

    public boolean hasActivateChannel(final String taskId) {
        return activeChannels.containsKey(taskId);
    }

    public void sendMessage(final String taskId, final AIResponseMessage message) {
        SseEmitter emitter = activeChannels.get(taskId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name(REDIS_CHANNEL)
                        .data(message));
                log.info("Sent message to SSE channel for taskId: {}", taskId);

                Long responseCount = redisStoreService.increment(responseCountKey(taskId), RESPONSE_COUNT_TTL);
                log.info("Response count for taskId {}: {}", taskId, responseCount);

                if (responseCount >= MAX_RESPONSE_COUNT) {
                    completeTask(taskId);
                }
            } catch (IOException e) {
                log.error("Failed to send message to SSE channel for taskId: {}", taskId, e);
                removeChannel(taskId);
            }
        }
    }

    public void removeChannel(final String taskId) {
        activeChannels.remove(taskId);
        redisStoreService.delete(sseChannelKey(taskId));
        log.info("Removed SSE channel for taskId: {}", taskId);
    }

    private void completeTask(final String taskId) {
        try {
            Long taskIdLong = Long.valueOf(taskId);
            aiTaskService.completeAITask(taskIdLong);
            log.info("Completed AI task for taskId: {}", taskId);

            SseEmitter emitter = activeChannels.get(taskId);
            if (emitter != null) {
                emitter.complete();
                log.info("Completed SSE channel for taskId: {}", taskId);
            }

            removeChannel(taskId);
            redisStoreService.delete(responseCountKey(taskId));

        } catch (Exception e) {
            log.error("Failed to complete task for taskId: {}", taskId, e);
        }
    }

    private String sseChannelKey(final String taskId) {
        return SSE_CHANNEL_PREFIX + taskId;
    }

    private String responseCountKey(final String taskId) {
        return RESPONSE_COUNT_PREFIX + taskId;
    }
}
