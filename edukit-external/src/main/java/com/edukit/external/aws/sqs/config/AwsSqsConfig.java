package com.edukit.external.aws.sqs.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
@EnableConfigurationProperties(AwsSqsProperties.class)
public class AwsSqsConfig {

    @Bean
    public SqsClient sqsClient(final AwsCredentialsProvider credentialsProvider,
                               final AwsSqsProperties properties) {
        return SqsClient.builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
