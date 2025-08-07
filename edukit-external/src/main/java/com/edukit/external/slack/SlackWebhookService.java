package com.edukit.external.slack;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class SlackWebhookService {

    @Value("${slack.webhook.url:}")
    private String webhookUrl;

    private final WebClient webClient;

    public SlackWebhookService() {
        this.webClient = WebClient.builder().build();
    }

    private static final String EMOJI_ALERT = "🚨";
    private static final String COLOR_DANGER = "danger";

    public void sendAlert(final String title, final String message) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            log.warn("Slack webhook URL이 설정되지 않았습니다. 알림을 보낼 수 없습니다.");
            return;
        }

        Map<String, Object> payload = Map.of(
                "text", EMOJI_ALERT + " " + title,
                "attachments", List.of(
                        Map.of(
                                "color", COLOR_DANGER,
                                "fields", List.of(
                                        Map.of("title", "Message", "value", message, "short", false),
                                        Map.of("title", "Time", "value", LocalDateTime.now().toString(), "short", true)
                                )
                        ))
        );

        webClient.post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        result -> log.debug("Slack 알림 전송 성공: {}", result.getStatusCode()),
                        error -> log.error("Slack 알림 전송 실패: {}", error.getMessage(), error)
                );
    }
}
