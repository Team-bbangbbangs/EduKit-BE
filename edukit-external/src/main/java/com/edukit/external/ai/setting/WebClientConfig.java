package com.edukit.external.ai.setting;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
@EnableConfigurationProperties(OpenAIProperties.class)
public class WebClientConfig {

    @Bean
    public WebClient openAIWebClient(final OpenAIProperties properties) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.connectionTimeout())
                .responseTimeout(Duration.ofMillis(properties.readTimeout()))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(properties.readTimeout(), TimeUnit.MILLISECONDS))
                                .addHandlerLast(
                                        new WriteTimeoutHandler(properties.readTimeout(), TimeUnit.MILLISECONDS)));

        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.apiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(loggingFilter())
                .clientConnector(connector)
                .build();
    }


    private ExchangeFilterFunction loggingFilter() {
        return (request, next) -> {
            long startTime = System.currentTimeMillis();
            return next.exchange(request)
                    .doOnSuccess(response -> {
                        long duration = System.currentTimeMillis() - startTime;
                        if (response != null && response.statusCode().is2xxSuccessful()) {
                            log.info("AI 호출 성공! 응답 시간: {}ms", duration);
                        } else {
                            log.error("AI 호출 응답 오류! 응답 시간: {}ms, 상태코드: {}",
                                    duration, response != null ? response.statusCode() : "NULL");
                        }
                    })
                    .doOnError(error -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.error("AI 호출 실패! 호출 시간: {}ms, 원인: {}", duration, error.getMessage());
                    });
        };
    }
}
