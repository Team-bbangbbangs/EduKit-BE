package com.edukit.external.aws.sqs;

import com.edukit.core.common.service.SqsService;
import com.edukit.external.aws.sqs.config.AwsSqsProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsServiceImpl implements SqsService {

    private final SqsClient sqsClient;
    private final AwsSqsProperties sqsProperties;
    private final ObjectMapper objectMapper;

    @Override
    public void sendMessage(final Object message) {
        try {
            final String messageBody = objectMapper.writeValueAsString(message);
            
            final SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(sqsProperties.queueUrl())
                    .messageBody(messageBody)
                    .build();

            final SendMessageResponse response = sqsClient.sendMessage(request);
            
            log.info("SQS 메시지 전송 완료 - MessageId: {}, QueueUrl: {}", 
                    response.messageId(), sqsProperties.queueUrl());
                    
        } catch (JsonProcessingException e) {
            log.error("SQS 메시지 직렬화 실패", e);
            throw new RuntimeException("SQS 메시지 전송 실패", e);
        } catch (Exception e) {
            log.error("SQS 메시지 전송 실패", e);
            throw new RuntimeException("SQS 메시지 전송 실패", e);
        }
    }
}
