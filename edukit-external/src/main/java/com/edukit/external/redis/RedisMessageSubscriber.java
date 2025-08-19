package com.edukit.external.redis;

import com.edukit.external.redis.exception.RedisErrorCode;
import com.edukit.external.redis.exception.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisMessageSubscriber implements MessageListener {

    @Override
    public void onMessage(final Message message, final byte[] pattern) {
        try {
            String msg = new String(message.getBody());
            log.info("Received message from Redis: {}", msg);
            // TODO: jobId → SSE push
        } catch (Exception e) {
            log.error("Error processing Redis message: {}", e.getMessage(), e);
            throw new RedisException(RedisErrorCode.MESSAGE_PROCESSING_FAILED);
        }
    }
}

