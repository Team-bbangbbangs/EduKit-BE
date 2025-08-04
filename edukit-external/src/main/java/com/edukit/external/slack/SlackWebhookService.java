package com.edukit.external.slack;

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

    public void sendAlert(final String title, final String message, final String level) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            log.warn("Slack webhook URL이 설정되지 않았습니다. 알림을 보낼 수 없습니다.");
            return;
        }

        try {
            String emoji = getEmojiByLevel(level);
            String color = getColorByLevel(level);

            String payload = String.format("""
                    {
                        "text": "%s %s",
                        "attachments": [
                            {
                                "color": "%s",
                                "fields": [
                                    {
                                        "title": "Details",
                                        "value": "%s",
                                        "short": false
                                    },
                                    {
                                        "title": "Time",
                                        "value": "%s",
                                        "short": true
                                    }
                                ]
                            }
                        ]
                    }
                    """, emoji, title, color, message, java.time.LocalDateTime.now());

            webClient.post().uri(webhookUrl).contentType(MediaType.APPLICATION_JSON).bodyValue(payload).retrieve()
                    .toBodilessEntity().block();

            log.info("Slack 알림 전송 성공: {}", title);

        } catch (Exception e) {
            log.error("Slack 알림 전송 실패: {}", e.getMessage(), e);
        }
    }

    private String getEmojiByLevel(String level) {
        return switch (level.toLowerCase()) {
            case "error", "critical" -> "🚨";
            case "warning" -> "⚠️";
            case "info" -> "ℹ️";
            default -> "📢";
        };
    }

    private String getColorByLevel(String level) {
        return switch (level.toLowerCase()) {
            case "error", "critical" -> "danger";
            case "warning" -> "warning";
            case "info" -> "good";
            default -> "#36a64f";
        };
    }
}
