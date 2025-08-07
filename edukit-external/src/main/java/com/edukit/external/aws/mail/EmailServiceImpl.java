package com.edukit.external.aws.mail;

import com.edukit.core.common.service.EmailService;
import com.edukit.external.aws.mail.exception.MailErrorCode;
import com.edukit.external.aws.mail.exception.MailException;
import com.edukit.external.aws.mail.setting.AwsSesEmailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.ApiCallTimeoutException;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final SesClient sesClient;
    private final AwsSesEmailMapper awsSesEmailMapper;

    public void sendEmail(final String emailReceiver, final String memberUuid, final String verificationCode) {
        SendEmailRequest request = awsSesEmailMapper.buildEmailRequestForSignUp(emailReceiver, memberUuid, verificationCode);
        send(request, emailReceiver);
    }

    private void send(final SendEmailRequest request, final String emailReceiver) {
        try {
            sesClient.sendEmail(request);
        } catch (ApiCallTimeoutException e) {
            throw new MailException(MailErrorCode.EMAIL_TIMEOUT, emailReceiver);
        } catch (Exception e) {
            throw new MailException(MailErrorCode.EMAIL_SEND_FAILED, emailReceiver);
        }
    }
}
