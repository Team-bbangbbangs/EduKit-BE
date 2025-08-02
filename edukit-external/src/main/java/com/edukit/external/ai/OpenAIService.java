package com.edukit.external.ai;

import com.edukit.external.ai.exception.OpenAiErrorCode;
import com.edukit.external.ai.exception.OpenAiException;
import com.edukit.external.ai.response.OpenAIVersionResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final ChatClient chatClient;
    private final CircuitBreaker openAiCircuitBreaker;

    private static final String SYSTEM_INSTRUCTIONS = """
            당신은 중고등학교 생활기록부 작성을 보조하는 AI 어시스턴트입니다.
            학생의 정보를 바탕으로 생활기록부를 작성합니다.
            """;

    private static final String FALLBACK_MESSAGE = "현재 AI 서비스에 일시적인 문제가 발생하여 요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요.";


    public Flux<OpenAIVersionResponse> getVersionedStreamingResponse(final String prompt) {
        return Flux.<OpenAIVersionResponse>create(sink -> {
                    StringBuilder buffer = new StringBuilder();
                    AtomicInteger currentVersion = new AtomicInteger(0);

                    chatClient.prompt()
                            .system(SYSTEM_INSTRUCTIONS)
                            .user(prompt)
                            .stream()
                            .content()
                            .subscribe(
                                    chunk -> {
                                        buffer.append(chunk);
                                        String currentBuffer = buffer.toString();

                                        if (isVersionComplete(currentBuffer, currentVersion.get())) {
                                            String completeVersion = extractCompleteVersion(currentBuffer,
                                                    currentVersion.get() + 1);

                                            if (completeVersion.isEmpty()) {
                                                sink.error(new OpenAiException(OpenAiErrorCode.OPEN_AI_INTERNAL_ERROR));
                                                return;
                                            }

                                            sink.next(OpenAIVersionResponse.of(
                                                    currentVersion.get() + 1,
                                                    completeVersion,
                                                    currentVersion.get() == 2
                                            ));

                                            currentVersion.incrementAndGet();
                                        }
                                    },
                                    sink::error,
                                    () -> {
                                        String finalBuffer = buffer.toString();

                                        if (currentVersion.get() < 3) {
                                            String version3Content = extractCompleteVersion(finalBuffer, 3);

                                            if (!version3Content.isEmpty()) {
                                                sink.next(OpenAIVersionResponse.of(
                                                        3,
                                                        version3Content,
                                                        true
                                                ));
                                            }
                                        }

                                        sink.complete();
                                    }
                            );
                })
                .transformDeferred(CircuitBreakerOperator.of(openAiCircuitBreaker))
                .doOnError(throwable -> log.error("OpenAI API 호출 중 오류 발생: {}", throwable.getMessage()))
                .onErrorResume(this::getFallbackResponse);
    }

    private boolean isVersionComplete(final String buffer, final int currentVersion) {
        String currentVersionPattern = "===VERSION_" + (currentVersion + 1) + "===";

        if (!buffer.contains(currentVersionPattern)) {
            return false;
        }

        if (currentVersion < 2) {
            String nextVersionPattern = "===VERSION_" + (currentVersion + 2) + "===";
            return buffer.contains(nextVersionPattern);
        }

        return false;
    }

    private Flux<OpenAIVersionResponse> getFallbackResponse(final Throwable throwable) {
        log.warn("OpenAI API 호출 실패로 인한 fallback 응답 제공. 오류: {}", throwable.getMessage());

        // 서킷 브레이커가 열려있는 경우와 기타 오류를 구분
        if (throwable instanceof CallNotPermittedException) {
            log.info("서킷 브레이커가 열려있어 fallback 응답을 제공합니다.");
        }

        return Flux.just(
                OpenAIVersionResponse.ofFallback(1, FALLBACK_MESSAGE + " (버전 1)", false),
                OpenAIVersionResponse.ofFallback(2, FALLBACK_MESSAGE + " (버전 2)", false),
                OpenAIVersionResponse.ofFallback(3, FALLBACK_MESSAGE + " (버전 3)", true)
        );
    }

    private String extractCompleteVersion(final String buffer, int versionNumber) {
        String versionPattern = "===VERSION_" + versionNumber + "===";
        String nextVersionPattern = "===VERSION_" + (versionNumber + 1) + "===";

        int startIndex = buffer.indexOf(versionPattern);
        if (startIndex == -1) {
            return "";
        }

        int contentStart = startIndex + versionPattern.length();
        int endIndex = buffer.indexOf(nextVersionPattern);

        if (endIndex != -1) {
            return buffer.substring(contentStart, endIndex).trim();
        } else {
            // 다음 버전이 없으면 끝까지 (주로 3번째 버전에서 발생)
            return buffer.substring(contentStart).trim();
        }
    }

    /* v1.0.0
    public OpenAIResponse getMultipleChatResponses(final String prompt) {
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
     */
}
