package com.edukit.external.redis;

import com.edukit.core.common.service.RedisStreamService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisStreamServiceImpl implements RedisStreamService {

    private final RedisTemplate<String, String> redisTemplate;

    public void createStreamConsumerGroup(final String streamKey, final String groupName, final ReadOffset readOffset) {
        redisTemplate.opsForStream().createGroup(streamKey, readOffset, groupName);
    }

    @SuppressWarnings("unchecked")
    public List<MapRecord<String, Object, Object>> readFromStream(final Consumer consumer, final String streamKey,
                                                                  final ReadOffset readOffset) {
        List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                .read(consumer, StreamOffset.create(streamKey, readOffset));
        return records != null ? records : Collections.emptyList();
    }

    public void acknowledgeStreamMessage(final String groupName, final String streamKey, final RecordId messageId) {
        redisTemplate.opsForStream().acknowledge(groupName, streamKey, messageId);
    }

    public void sendMessage(final String streamKey, final Map<String, Object> messageData) {
        redisTemplate.opsForStream().add(streamKey, messageData);
    }
}
