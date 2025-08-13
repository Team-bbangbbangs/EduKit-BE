package com.edukit.external.aws.mail.config;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AwsSesProperties.class)
public class AwsSesConfig {

    @Bean
    SesClient createSesClient(final AwsCredentialsProvider credentialsProvider, final AwsSesProperties properties) {
        return SesClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(properties.region()))
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .apiCallAttemptTimeout(Duration.ofSeconds(properties.singleRequestTimeout()))
                        .apiCallTimeout(Duration.ofSeconds(properties.totalCallTimeout()))
                        .build())
                .build();
    }
}
