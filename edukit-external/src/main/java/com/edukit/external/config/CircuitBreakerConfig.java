package com.edukit.external.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CircuitBreakerConfig {

    @Bean
    public CircuitBreaker openAiCircuitBreaker(final CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("openai");

        circuitBreaker.getEventPublisher()
                .onStateTransition(event ->
                        log.info("OpenAI Circuit breaker state transition: {} -> {}",
                                event.getStateTransition().getFromState(),
                                event.getStateTransition().getToState()))
                .onCallNotPermitted(event ->
                        log.warn("OpenAI Circuit breaker call not permitted"))
                .onError(event ->
                        log.error("OpenAI Circuit breaker error: {}", event.getThrowable().getMessage()))
                .onSuccess(event ->
                        log.debug("OpenAI Circuit breaker success"));

        return circuitBreaker;
    }
}
