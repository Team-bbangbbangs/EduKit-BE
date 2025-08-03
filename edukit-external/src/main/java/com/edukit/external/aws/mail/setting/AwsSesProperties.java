package com.edukit.external.aws.mail.setting;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("aws.ses")
public record AwsSesProperties(
        String senderEmail,
        String region,
        String emailVerifyUrl
) {
}
