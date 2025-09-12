package com.edukit.external.aws.mail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("aws.ses")
public record AwsSesProperties(
        String senderEmail,
        String region,
        String teacherVerifyUrl,
        String passwordResetUrl,
        int singleRequestTimeout,
        int totalCallTimeout
) {
}
