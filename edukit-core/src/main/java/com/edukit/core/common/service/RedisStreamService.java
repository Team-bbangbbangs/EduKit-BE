package com.edukit.core.common.service;

import java.util.List;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;

public interface RedisStreamService {

    void createStreamConsumerGroup(String streamKey, String groupName, ReadOffset readOffset);

    List<MapRecord<String, Object, Object>> readFromStream(Consumer consumer, String streamKey, ReadOffset readOffset);

    void acknowledgeStreamMessage(String groupName, String streamKey, RecordId messageId);
}
