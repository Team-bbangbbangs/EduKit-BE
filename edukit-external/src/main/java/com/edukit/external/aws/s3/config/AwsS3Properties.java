package com.edukit.external.aws.s3.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("aws.s3")
public record AwsS3Properties(
        String region,
        String bucket,
        String cdnUrl
) {
}
