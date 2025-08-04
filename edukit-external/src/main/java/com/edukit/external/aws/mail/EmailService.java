package com.edukit.external.aws.mail;

import com.edukit.external.aws.mail.exception.MailErrorCode;
import com.edukit.external.aws.mail.exception.MailException;
import com.edukit.external.aws.mail.setting.AwsSesEmailMapper;
import com.edukit.external.slack.SlackWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.exception.ApiCallTimeoutException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;
import software.amazon.awssdk.services.ses.model.SesException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final SesClient sesClient;
    private final AwsSesEmailMapper awsSesEmailMapper;
    private final SlackWebhookService slackWebhookService;

    public void sendEmail(final String emailReceiver, final String memberUuid, final String verificationCode) {
        SendEmailRequest request = awsSesEmailMapper.buildEmailRequestForSignUp(emailReceiver, memberUuid, verificationCode);
        send(request, emailReceiver);
    }

    private void send(final SendEmailRequest request, final String emailReceiver) {
        try {
            log.info("[SES] 이메일 발송 시작: to={}", emailReceiver);
            SendEmailResponse result = sesClient.sendEmail(request);
            validateSendResult(emailReceiver, result);
            
        } catch (ApiCallTimeoutException e) {
            // 애플리케이션 레벨 타임아웃 (우리가 설정한 타임아웃)
            String errorMessage = String.format("이메일 발송 타임아웃: to=%s, timeout=%s", 
                emailReceiver, e.getMessage());
            log.error(errorMessage, e);
            
            // Slack 알림 발송
            slackWebhookService.sendAlert(
                "📧 이메일 발송 타임아웃 발생",
                String.format("수신자: %s\\n에러: %s", emailReceiver, e.getMessage()),
                "error"
            );
            
            throw new MailException(MailErrorCode.EMAIL_TIMEOUT, e);
            
        } catch (SesException e) {
            String errorMessage = String.format("SES 서비스 에러: to=%s, awsErrorCode=%s, message=%s", 
                emailReceiver, e.awsErrorDetails().errorCode(), e.getMessage());
            log.error(errorMessage, e);
            
            // SDK 재시도 실패 감지 (보통 4xx, 5xx 에러)
            if (isSdkRetryExhausted(e)) {
                slackWebhookService.sendAlert(
                    "📧 이메일 발송 재시도 모두 실패",
                    String.format("수신자: %s\\n에러코드: %s\\n메시지: %s", 
                        emailReceiver, e.awsErrorDetails().errorCode(), e.getMessage()),
                    "critical"
                );
                throw new MailException(MailErrorCode.EMAIL_SDK_RETRY_EXHAUSTED);
            }
            
            throw new MailException(MailErrorCode.EMAIL_SEND_FAILED);
            
        } catch (SdkClientException e) {
            String errorMessage = String.format("AWS SDK 클라이언트 에러: to=%s, message=%s", 
                emailReceiver, e.getMessage());
            log.error(errorMessage, e);
            
            slackWebhookService.sendAlert(
                "📧 이메일 발송 연결 실패",
                String.format("수신자: %s\\n에러: %s", emailReceiver, e.getMessage()),
                "error"
            );
            
            throw new MailException(MailErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private void validateSendResult(final String emailReceiver, final SdkResponse result) {
        SdkHttpResponse httpResponse = result.sdkHttpResponse();
        if (httpResponse.isSuccessful()) {
            log.info("[SES] 이메일 발송 성공: to={}, messageId={}", 
                emailReceiver, ((SendEmailResponse) result).messageId());
        } else {
            String errorReason = httpResponse.statusText().orElse("unknown");
            log.error("[SES] 이메일 발송 실패: to={}, statusCode={}, reason={}", 
                emailReceiver, httpResponse.statusCode(), errorReason);
            
            // HTTP 상태 코드 기반 Slack 알림
            slackWebhookService.sendAlert(
                "📧 이메일 발송 HTTP 에러",
                String.format("수신자: %s\\n상태코드: %d\\n이유: %s", 
                    emailReceiver, httpResponse.statusCode(), errorReason),
                "error"
            );
            
            throw new MailException(MailErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private boolean isSdkRetryExhausted(SesException e) {
        // 일반적으로 SDK가 재시도를 모두 소진한 경우의 조건들
        String errorCode = e.awsErrorDetails().errorCode();
        
        // 4xx, 5xx 에러 중 재시도 가능한 것들이 모두 실패한 경우
        return "ServiceUnavailable".equals(errorCode) ||
               "Throttling".equals(errorCode) ||
               "TooManyRequestsException".equals(errorCode) ||
               "InternalServerError".equals(errorCode) ||
               e.getMessage().contains("exceeded") ||
               e.getMessage().contains("retry");
    }
}
