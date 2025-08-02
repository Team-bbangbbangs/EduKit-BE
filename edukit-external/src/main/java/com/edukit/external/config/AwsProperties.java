package com.edukit.external.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("aws.common")
public record AwsProperties(
        String accessKey,
        String secretKey
) {
}
