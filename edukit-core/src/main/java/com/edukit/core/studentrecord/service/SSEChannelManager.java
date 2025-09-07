package com.edukit.core.studentrecord.service;

import com.edukit.common.infra.ServerInstanceManager;
import com.edukit.core.common.event.ai.dto.AIProgressMessage;
import com.edukit.core.common.event.ai.dto.AIResponseMessage;
import com.edukit.core.common.event.ai.dto.SSEMessage;
import com.edukit.core.common.service.RedisStoreService;
import com.edukit.core.studentrecord.exception.StudentRecordErrorCode;
import com.edukit.core.studentrecord.exception.StudentRecordException;
import com.edukit.core.studentrecord.service.enums.AITaskStatus;
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
    private static final String TASK_STATUS_PREFIX = "task-status:";
    private static final String SSE_EVENT_NAME = "ai-message";
    private static final int MAX_RESPONSE_COUNT = 3;
    private static final Duration RESPONSE_COUNT_TTL = Duration.ofMinutes(5);
    private static final Duration TASK_STATUS_TTL = Duration.ofHours(1);

    public void registerTaskChannel(final String taskId, final SseEmitter emitter) {
        String serverId = serverInstanceManager.getServerId();
        redisStoreService.store(sseChannelKey(taskId), serverId, Duration.ofHours(1));
        activeChannels.put(taskId, emitter);
        log.info("Registered SSE channel for taskId: {} on server: {}", taskId, serverId);

        // SSE 채널 등록 시 현재 진행 상태가 있다면 전송
        String currentStatus = redisStoreService.get(taskStatusKey(taskId));
        if (currentStatus != null) {
            try {
                String message = AITaskStatus.getMessageByStatus(currentStatus);
                SSEMessage sseMessage = SSEMessage.progress(taskId, message);
                emitter.send(SseEmitter.event()
                        .name(SSE_EVENT_NAME)
                        .data(sseMessage));
                log.info("Sent stored progress message to SSE channel for taskId: {}, message: {}", taskId,
                        currentStatus);
            } catch (IOException e) {
                log.error("Failed to send stored progress message to SSE channel for taskId: {}", taskId, e);
                removeChannel(taskId);
            }
        }
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
                SSEMessage sseMessage = SSEMessage.response(message.taskId(), message.reviewedContent(),
                        message.version());
                emitter.send(SseEmitter.event()
                        .name(SSE_EVENT_NAME)
                        .data(sseMessage));
                log.info("Sent response message to SSE channel for taskId: {}", taskId);

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

    public void sendProgressMessage(final String taskId, final AIProgressMessage aiProgressMessage) {
        String message = aiProgressMessage.status().getMessage();
        String status = aiProgressMessage.status().getStatus();

        // Redis에 진행 상태 저장 (SSE 채널이 없어도 저장)
        redisStoreService.store(taskStatusKey(taskId), status, TASK_STATUS_TTL);
        log.info("Stored progress message in Redis for taskId: {}, message: {}", taskId, message);

        SseEmitter emitter = activeChannels.get(taskId);
        if (emitter != null) {
            try {
                SSEMessage sseMessage = SSEMessage.progress(aiProgressMessage.taskId(), message);
                emitter.send(SseEmitter.event()
                        .name(SSE_EVENT_NAME)
                        .data(sseMessage));
                log.info("Sent progress message to SSE channel for taskId: {}, message: {}", taskId, message);
            } catch (IOException e) {
                log.error("Failed to send progress message to SSE channel for taskId: {}", taskId, e);
                removeChannel(taskId);
            }
        } else {
            log.info("No active SSE channel for taskId: {}, message stored in Redis", taskId);
        }
    }

    public void removeChannel(final String taskId) {
        SseEmitter emitter = activeChannels.remove(taskId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.warn("SSE complete failed for taskId: {}", taskId, e);
            } finally {
                redisStoreService.delete(sseChannelKey(taskId));
                redisStoreService.delete(taskStatusKey(taskId));
                log.info("Removed SSE channel for taskId: {}", taskId);
            }
        }
    }

    private void completeTask(final String taskId) {
        try {
            Long taskIdLong = Long.valueOf(taskId);
            aiTaskService.completeAITask(taskIdLong);
            log.info("Completed AI task for taskId: {}", taskId);
        } catch (Exception e) {
            log.error("Failed to complete task for taskId: {}", taskId, e);
            throw new StudentRecordException(StudentRecordErrorCode.AI_TASK_COMPLETION_FAILED, e);
        } finally {
            removeChannel(taskId);
        }
    }

    private String sseChannelKey(final String taskId) {
        return SSE_CHANNEL_PREFIX + taskId;
    }

    private String responseCountKey(final String taskId) {
        return RESPONSE_COUNT_PREFIX + taskId;
    }

    private String taskStatusKey(final String taskId) {
        return TASK_STATUS_PREFIX + taskId;
    }
}
