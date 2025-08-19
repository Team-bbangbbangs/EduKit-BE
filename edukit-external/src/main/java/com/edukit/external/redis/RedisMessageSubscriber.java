package com.edukit.external.redis;

import com.edukit.common.ServerInstanceManager;
import com.edukit.core.common.event.ai.dto.AIResponseMessage;
import com.edukit.core.studentrecord.service.SSEChannelManager;
import com.edukit.external.redis.exception.RedisErrorCode;
import com.edukit.external.redis.exception.RedisException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final ServerInstanceManager serverInstanceManager;
    private final SSEChannelManager sseChannelManager;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(final Message message, final byte[] pattern) {
        try {
            String msg = new String(message.getBody());
            log.info("Received message from Redis: {}", msg);

            AIResponseMessage responseMessage = objectMapper.readValue(msg, AIResponseMessage.class);
            String taskId = String.valueOf(responseMessage.taskId());

            if (sseChannelManager.hasActivateChannel(taskId)) {
                sseChannelManager.sendMessage(taskId, responseMessage);
            } else {
                String targetServerId = sseChannelManager.get(taskId);

                if (targetServerId != null &&
                        targetServerId.equals(serverInstanceManager.getServerId())) {
                    log.warn("Task {} assigned to current server but no active channel", taskId);
                    sseChannelManager.deleteChannel(taskId);
                }
            }
        } catch (Exception e) {
            log.error("Error processing Redis message: {}", e.getMessage(), e);
            throw new RedisException(RedisErrorCode.MESSAGE_PROCESSING_FAILED);
        }
    }
}

