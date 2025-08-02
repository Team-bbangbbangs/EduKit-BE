package com.edukit.external.aws.mail.config;

import com.edukit.external.aws.mail.setting.AwsSesProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
@EnableConfigurationProperties(AwsSesProperties.class)
public class AwsSesConfig {

    @Bean
    SesClient createSesClient(final AwsSesProperties properties, final AwsCredentialsProvider credentialsProvider) {
        return SesClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(properties.region()))
                .build();
    }
}
