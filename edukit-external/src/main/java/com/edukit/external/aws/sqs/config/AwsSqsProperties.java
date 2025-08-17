package com.edukit.external.aws.sqs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.sqs")
public record AwsSqsProperties(
        String region,
        String queueUrl
) {
}
