package com.edukit.external.openai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;

    private static final String SYSTEM_INSTRUCTIONS = """
            당신은 중고등학교 생활기록부 작성을 보조하는 AI 어시스턴트입니다.
            학생의 정보를 바탕으로 생활기록부를 작성합니다.
            """;

    public StudentRecordAICreateResponse getThreeChatResponses(final String prompt) {
        return getMultipleChatResponses(prompt);
    }

    private StudentRecordAICreateResponse getMultipleChatResponses(final String prompt) {
        return chatClient.prompt()
                .system(SYSTEM_INSTRUCTIONS)
                .user(prompt)
                .call()
                .entity(StudentRecordAICreateResponse.class);
    }
}
