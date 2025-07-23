package com.edukit.external.config;

import com.edukit.external.openai.OpenAiProperties;
import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;

@Slf4j
@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
public class SpringAiConfig {

    @Bean
    public RestClientCustomizer restClientCustomizer(final OpenAiProperties properties) {
        return restClientBuilder -> {
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(properties.connectTimeout()))
                    .build();

            JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
            factory.setReadTimeout(Duration.ofSeconds(properties.readTimeout()));

            restClientBuilder
                    .requestFactory(factory)
                    .defaultHeaders(headers -> {
                        headers.add("User-Agent", "EduKit-External/1.0");
                    })
                    .requestInterceptor(this::logRequest);
        };
    }

    private ClientHttpResponse logRequest(final HttpRequest request, final byte[] body,
                                          final ClientHttpRequestExecution execution) throws IOException {
        long startTime = System.currentTimeMillis();
        try {
            ClientHttpResponse response = execution.execute(request, body);
            long duration = System.currentTimeMillis() - startTime;
            log.info("AI 호출 성공! 응답 시간: {}ms", duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("AI 호출 실패! 호출 시간: {}ms: {}", duration, e.getMessage());
            throw e;
        }
    }

    @Bean
    public ChatClient chatClient(final ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
