package com.edukit.external.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Configuration
@EnableConfigurationProperties(AwsProperties.class)
public class AwsCredentialConfig {

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(final AwsProperties properties) {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                        properties.accessKey(),
                        properties.secretKey()
                )
        );
    }
}
