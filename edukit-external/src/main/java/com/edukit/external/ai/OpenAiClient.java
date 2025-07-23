package com.edukit.external.ai;

import com.edukit.external.ai.dto.response.StudentRecordAICreateResponse;
import com.edukit.external.ai.exception.OpenAiException;
import com.edukit.external.ai.exception.OpenAiErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final ChatClient chatClient;

    private static final String SYSTEM_INSTRUCTIONS = """
            당신은 중고등학교 생활기록부 작성을 보조하는 AI 어시스턴트입니다.
            학생의 정보를 바탕으로 생활기록부를 작성합니다.
            """;

    public StudentRecordAICreateResponse getThreeAIResponses(final String prompt) {
        return getMultipleChatResponses(prompt);
    }

    private StudentRecordAICreateResponse getMultipleChatResponses(final String prompt) {
        try {
            return chatClient.prompt()
                    .system(SYSTEM_INSTRUCTIONS)
                    .user(prompt)
                    .call()
                    .entity(StudentRecordAICreateResponse.class);
        } catch (ResourceAccessException e) { // 타임 아웃
            throw new OpenAiException(OpenAiErrorCode.OPEN_AI_TIMEOUT);
        } catch (Exception e) { // 기타 예외 처리
            throw new OpenAiException(OpenAiErrorCode.OPEN_AI_INTERNAL_ERROR);
        }
    }
}
