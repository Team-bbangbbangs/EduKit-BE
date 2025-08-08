package com.edukit.external.common.handler;

import com.edukit.external.slack.SlackWebhookService;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final SlackWebhookService slackWebhookService;

    @Override
    public void handleUncaughtException(final Throwable ex, final Method method, final Object... params) {
        String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();

        log.error("[비동기 작업 실패] method={}, error={}", methodName, ex.getMessage(), ex);

        sendErrorNotification(methodName, ex);
    }

    private void sendErrorNotification(final String methodName, final Throwable ex) {
        try {
            slackWebhookService.sendAlert(methodName, ex.getMessage());
        } catch (Exception e) {
            log.warn("Slack 알림 전송 실패: {}", e.getMessage());
        }
    }
}
