package com.edukit.external.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("aws.s3")
public record AwsS3Properties(
    String accessKey,
    String secretKey,
    String region,
    String bucket,
    String cdnUrl
) {
}
