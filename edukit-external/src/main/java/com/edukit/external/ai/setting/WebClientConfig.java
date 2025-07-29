package com.edukit.external.ai.setting;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
@EnableConfigurationProperties(OpenAIProperties.class)
public class WebClientConfig {

    @Bean
    public WebClientCustomizer openAIWebClientCustomizer(final OpenAIProperties properties) {
        return webClientBuilder -> {
            HttpClient httpClient = HttpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.connectionTimeout())
                    .responseTimeout(Duration.ofMillis(properties.readTimeout())).doOnConnected(
                            conn -> conn.addHandlerLast(
                                            new ReadTimeoutHandler(properties.readTimeout(), TimeUnit.MILLISECONDS))
                                    .addHandlerLast(
                                            new WriteTimeoutHandler(properties.readTimeout(), TimeUnit.MILLISECONDS)));

            ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

            webClientBuilder.baseUrl(properties.baseUrl())
                    .clientConnector(connector)
                    .defaultHeader("User-Agent", "EduKit-External/1.0")
                    .build();
        };
    }
}
