package com.edukit.external.ai;

import com.edukit.external.ai.response.OpenAIVersionResponse;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final ChatClient chatClient;

    private static final String SYSTEM_INSTRUCTIONS = """
            당신은 중고등학교 생활기록부 작성을 보조하는 AI 어시스턴트입니다.
            학생의 정보를 바탕으로 생활기록부를 작성합니다.
            """;


    public Flux<OpenAIVersionResponse> getVersionedStreamingResponse(final String prompt) {
        return Flux.create(sink -> {
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

                                // 버전 완성 감지 및 전송 로직
                                if (isVersionComplete(currentBuffer, currentVersion.get())) {
                                    String completeVersion = extractCompleteVersion(currentBuffer,
                                            currentVersion.get() + 1);

                                    sink.next(OpenAIVersionResponse.of(
                                            currentVersion.get() + 1,
                                            completeVersion,
                                            currentVersion.get() == 2 // 3번째 버전이면 마지막
                                    ));

                                    currentVersion.incrementAndGet();
                                }
                            },
                            sink::error,
                            () -> {
                                // 스트림 완료 시 3번째 버전 처리
                                String finalBuffer = buffer.toString();

                                // 아직 처리하지 않은 버전이 있다면 처리
                                if (currentVersion.get() < 3) {
                                    String version3Content = extractCompleteVersion(finalBuffer, 3);

                                    if (!version3Content.isEmpty()) {
                                        sink.next(OpenAIVersionResponse.of(
                                                3,
                                                version3Content,
                                                true // 마지막 버전
                                        ));
                                    }
                                }

                                sink.complete();
                            }
                    );
        });
    }

    private boolean isVersionComplete(String buffer, int currentVersion) {
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

    private String extractCompleteVersion(String buffer, int versionNumber) {
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
