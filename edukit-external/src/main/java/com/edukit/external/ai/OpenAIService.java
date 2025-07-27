package com.edukit.external.ai;

import com.edukit.external.ai.response.OpenAIResponse;
import com.edukit.external.ai.exception.OpenAiErrorCode;
import com.edukit.external.ai.exception.OpenAiException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Flux;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final ChatClient chatClient;

    private static final String SYSTEM_INSTRUCTIONS = """
            당신은 중고등학교 생활기록부 작성을 보조하는 AI 어시스턴트입니다.
            학생의 정보를 바탕으로 생활기록부를 작성합니다.
            """;

    public OpenAIResponse getThreeAIResponses(final String prompt) {
        return getMultipleChatResponses(prompt);
    }

    public Flux<String> getStreamingResponse(final String prompt) {
        try {
            return chatClient.prompt()
                    .system(SYSTEM_INSTRUCTIONS)
                    .user(prompt)
                    .stream()
                    .content();
        } catch (ResourceAccessException ex) {
            throw new OpenAiException(OpenAiErrorCode.OPEN_AI_TIMEOUT, ex);
        } catch (Exception e) {
            throw new OpenAiException(OpenAiErrorCode.OPEN_AI_INTERNAL_ERROR, e);
        }
    }

    private OpenAIResponse getMultipleChatResponses(final String prompt) {
        try {
            return chatClient.prompt()
                    .system(SYSTEM_INSTRUCTIONS)
                    .user(prompt)
                    .call()
                    .entity(OpenAIResponse.class);
        } catch (ResourceAccessException ex) { // 타임 아웃
            throw new OpenAiException(OpenAiErrorCode.OPEN_AI_TIMEOUT, ex);
        } catch (Exception e) { // 기타 예외 처리
            throw new OpenAiException(OpenAiErrorCode.OPEN_AI_INTERNAL_ERROR, e);
        }
    }
}
