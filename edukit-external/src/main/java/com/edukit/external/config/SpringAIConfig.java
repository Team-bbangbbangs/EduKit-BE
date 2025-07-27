package com.edukit.external.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SpringAIConfig {

    @Bean
    public ChatClient chatClient(final ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
