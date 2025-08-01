package com.edukit.external.aws.mail;

import com.edukit.external.aws.mail.exception.MailErrorCode;
import com.edukit.external.aws.mail.exception.MailException;
import com.edukit.external.aws.mail.setting.AwsSesEmailMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final SesClient sesClient;
    private final AwsSesEmailMapper awsSesEmailMapper;

    public void sendEmail(final String emailReceiver, final String memberUuid, final String verificationCode) {
        SendEmailRequest request = awsSesEmailMapper.buildEmailRequestForSignUp(emailReceiver, memberUuid,
                verificationCode);
        send(request, emailReceiver, memberUuid);
    }

    private void send(final SendEmailRequest request, final String emailReceiver, final String memberUuid) {
        SendEmailResponse result = sesClient.sendEmail(request);
        validateSendResult(emailReceiver, result);
    }

    private void validateSendResult(final String emailReceiver, final SdkResponse result) {
        SdkHttpResponse httpResponse = result.sdkHttpResponse();
        if (httpResponse.isSuccessful()) {
            log.info("[SES] 이메일 발송 성공: to={}", emailReceiver);
        } else {
            log.error("[SES] 이메일 발송 실패: to={}, reason={}", emailReceiver, httpResponse.statusText().orElse("unknown"));
            throw new MailException(MailErrorCode.EMAIL_SEND_FAILED);
        }
    }
}
