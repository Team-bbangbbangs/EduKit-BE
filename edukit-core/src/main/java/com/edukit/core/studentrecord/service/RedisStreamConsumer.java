package com.edukit.core.studentrecord.service;

import com.edukit.common.ServerInstanceManager;
import com.edukit.core.common.event.ai.dto.AIResponseMessage;
import com.edukit.core.common.service.RedisStreamService;
import com.edukit.core.studentrecord.exception.StudentRecordErrorCode;
import com.edukit.core.studentrecord.exception.StudentRecordException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(RedisStreamService.class)
public class RedisStreamConsumer {

    private final RedisStreamService redisStreamServiceImpl;
    private final ServerInstanceManager serverInstanceManager;
    private final SSEChannelManager sseChannelManager;
    private final ObjectMapper objectMapper;

    private static final String STREAM_KEY = "ai-response";
    private static final String CONSUMER_GROUP_PREFIX = "edukit-server-";
    private static final String CONSUMER_NAME = "consumer-1";

    private ScheduledExecutorService executorService;
    private String consumerGroupName;

    @PostConstruct
    public void initialize() {
        this.consumerGroupName = CONSUMER_GROUP_PREFIX + serverInstanceManager.getServerId();
        this.executorService = Executors.newSingleThreadScheduledExecutor();

        createConsumerGroupIfNotExists();
        startConsuming();
    }

    @PreDestroy
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void createConsumerGroupIfNotExists() {
        try {
            redisStreamServiceImpl.createStreamConsumerGroup(STREAM_KEY, consumerGroupName, ReadOffset.from("0"));
            log.info("Created consumer group: {} for stream: {}", consumerGroupName, STREAM_KEY);
        } catch (Exception e) {
            log.debug("Consumer group {} already exists or stream doesn't exist yet", consumerGroupName);
        }
    }

    private void startConsuming() {
        executorService.scheduleWithFixedDelay(this::consumeMessages, 0, 1, TimeUnit.SECONDS);
        log.info("Started Redis Stream consumer for group: {}", consumerGroupName);
    }

    private void consumeMessages() {
        try {
            Consumer consumer = Consumer.from(consumerGroupName, CONSUMER_NAME);
            List<MapRecord<String, Object, Object>> messages = redisStreamServiceImpl.readFromStream(consumer,
                    STREAM_KEY,
                    ReadOffset.lastConsumed());

            for (MapRecord<String, Object, Object> message : messages) {
                processMessage(message);
                acknowledgeMessage(message.getId());
            }
        } catch (Exception e) {
            log.error("Error consuming messages from Redis Stream: {}", e.getMessage(), e);
        }
    }

    private void processMessage(final MapRecord<String, Object, Object> message) {
        try {
            Map<Object, Object> messageBody = message.getValue();
            String messageJson = (String) messageBody.get("data");

            log.info("Received message from Redis Stream: {}", messageJson);

            AIResponseMessage responseMessage = objectMapper.readValue(messageJson, AIResponseMessage.class);
            String taskId = String.valueOf(responseMessage.taskId());

            if (sseChannelManager.hasActivateChannel(taskId)) {
                sseChannelManager.sendMessage(taskId, responseMessage);
                log.info("Message sent to active SSE channel for taskId: {}", taskId);
            } else {
                String targetServerId = sseChannelManager.get(taskId);
                if (targetServerId != null && targetServerId.equals(serverInstanceManager.getServerId())) {
                    log.warn("Task {} assigned to current server but no active channel", taskId);
                    sseChannelManager.deleteChannel(taskId);
                }
            }
        } catch (Exception e) {
            log.error("Error processing Redis Stream message: {}", e.getMessage(), e);
            throw new StudentRecordException(StudentRecordErrorCode.MESSAGE_PROCESSING_FAILED);
        }
    }

    private void acknowledgeMessage(final RecordId messageId) {
        try {
            redisStreamServiceImpl.acknowledgeStreamMessage(consumerGroupName, STREAM_KEY, messageId);
            log.debug("Acknowledged message: {}", messageId);
        } catch (Exception e) {
            log.error("Failed to acknowledge message: {}", messageId, e);
        }
    }
}

