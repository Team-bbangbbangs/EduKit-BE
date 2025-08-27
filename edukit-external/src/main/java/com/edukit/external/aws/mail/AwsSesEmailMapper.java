package com.edukit.external.aws.mail;

import com.edukit.external.aws.mail.config.AwsSesProperties;
import com.edukit.external.aws.mail.exception.MailErrorCode;
import com.edukit.external.aws.mail.exception.MailException;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

@Component
@RequiredArgsConstructor
public class AwsSesEmailMapper {

    private final TemplateEngine templateEngine;
    private final AwsSesProperties awsSesProperties;

    private static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();

    public SendEmailRequest buildEmailRequestForTeacherVerify(final String emailReceiver, final String memberUuid,
                                                              final String verificationCode, final String subject) {
        String htmlBody = buildVerificationEmail(memberUuid, verificationCode);
        return buildSendEmailRequest(emailReceiver, subject, htmlBody);
    }

    private String buildVerificationEmail(final String memberUuid, final String verificationCode) {
        Context context = new Context();
        context.setVariable("verificationLink", buildVerificationUrl(memberUuid, verificationCode));
        return templateEngine.process("email-verification", context);
    }

    public SendEmailRequest buildSendEmailRequest(final String emailReceiver, final String subject,
                                                  final String htmlBody) {
        Destination destination = createDestination(emailReceiver);
        Content subjectContent = createSubjectContent(subject);
        Content htmlBodyContent = createHtmlBodyContent(htmlBody);
        Body body = createBody(htmlBodyContent);
        Message message = createMessage(subjectContent, body);

        return SendEmailRequest.builder()
                .source(awsSesProperties.senderEmail())
                .destination(destination)
                .message(message)
                .build();
    }

    private String buildVerificationUrl(final String memberUuid, final String verificationCode) {
        try {
            return String.format(awsSesProperties.emailVerifyUrl(), memberUuid, verificationCode);
        } catch (IllegalFormatException e) {
            throw new MailException(MailErrorCode.ILLEGAL_URL_ARGUMENT);
        }
    }

    private Destination createDestination(final String emailReceiver) {
        return Destination.builder()
                .toAddresses(emailReceiver)
                .build();
    }

    private Content createSubjectContent(final String subject) {
        return Content.builder()
                .data(subject)
                .charset(DEFAULT_CHARSET)
                .build();
    }

    private Content createHtmlBodyContent(final String htmlBody) {
        return Content.builder()
                .data(htmlBody)
                .charset(DEFAULT_CHARSET)
                .build();
    }

    private Body createBody(final Content htmlBodyContent) {
        return Body.builder()
                .html(htmlBodyContent)
                .build();
    }

    private Message createMessage(final Content subjectContent, final Body body) {
        return Message.builder()
                .subject(subjectContent)
                .body(body)
                .build();
    }
}
