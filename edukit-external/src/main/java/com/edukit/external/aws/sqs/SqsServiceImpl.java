package com.edukit.external.aws.sqs;

import com.edukit.core.common.service.SqsService;
import com.edukit.external.aws.sqs.config.AwsSqsProperties;
import com.edukit.external.aws.sqs.exception.SQSErrorCode;
import com.edukit.external.aws.sqs.exception.SQSException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsServiceImpl implements SqsService {

    private final SqsClient sqsClient;
    private final AwsSqsProperties sqsProperties;
    private final ObjectMapper objectMapper;

    private static final int SQS_MAX_MESSAGE_SIZE = 256 * 1024;

    @Override
    public void sendMessage(final Object message) {
        try {
            final String messageBody = objectMapper.writeValueAsString(message);
            final int messageSizeBytes = messageBody.getBytes(StandardCharsets.UTF_8).length;

            log.debug("SQS 메시지 크기: {} bytes (제한: {} bytes)", messageSizeBytes, SQS_MAX_MESSAGE_SIZE);

            validateMessageSize(messageSizeBytes);

            sendMessageInternal(messageBody);

        } catch (JsonProcessingException e) {
            log.error("SQS 메시지 직렬화 실패", e);
            throw new SQSException(SQSErrorCode.MESSAGE_SERIALIZATION_FAILED, e);
        } catch (SqsException e) {
            log.error("SQS 전송 실패 - 상태코드: {}, 에러코드: {}, 메시지: {}",
                    e.statusCode(), e.awsErrorDetails().errorCode(), e.awsErrorDetails().errorMessage(), e);
            throw new SQSException(SQSErrorCode.MESSAGE_SEND_FAIL, e);
        } catch (Exception e) {
            log.error("SQS 메시지 전송 실패", e);
            throw new SQSException(SQSErrorCode.MESSAGE_SEND_FAIL, e);
        }
    }

    private void validateMessageSize(final int messageSizeBytes) {
        if (messageSizeBytes > SQS_MAX_MESSAGE_SIZE) {
            log.error("메시지 크기가 SQS 제한(256KB)을 초과했습니다 - 메시지 크기 초과로 전송 실패: {} bytes", messageSizeBytes);
            throw new SQSException(SQSErrorCode.MESSAGE_SIZE_EXCEEDED);
        }
    }

    private void sendMessageInternal(final String messageBody) {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        
        // MDC에서 TraceId 가져와서 메시지 속성으로 추가
        String traceId = MDC.get("traceId");
        if (traceId != null) {
            messageAttributes.put("traceId", MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue(traceId)
                    .build());
        }
        
        // MDC에서 UserId도 함께 전달
        String userId = MDC.get("userId");
        if (userId != null) {
            messageAttributes.put("userId", MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue(userId)
                    .build());
        }

        final SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(sqsProperties.queueUrl())
                .messageBody(messageBody)
                .messageAttributes(messageAttributes)
                .build();

        final SendMessageResponse response = sqsClient.sendMessage(request);
        log.info("SQS 메시지 전송 완료 - MessageId: {}, TraceId: {}", response.messageId(), traceId);
    }
}
