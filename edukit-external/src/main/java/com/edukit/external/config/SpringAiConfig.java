package com.edukit.external.config;

import com.edukit.external.openai.OpenAiProperties;
import java.time.Duration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
public class SpringAiConfig {

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(final OpenAiProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(properties.connectTimeout()));
        factory.setReadTimeout(Duration.ofSeconds(properties.readTimeout()));
        return factory;
    }

    @Bean
    public RestClientCustomizer restClientCustomizer(final ClientHttpRequestFactory requestFactory) {
        return restClientBuilder -> {
            restClientBuilder
                .requestFactory(requestFactory)
                .defaultHeaders(headers -> {
                    headers.add("User-Agent", "EduKit-External/1.0");
                });
        };
    }

    @Bean
    public ChatClient chatClient(final ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
