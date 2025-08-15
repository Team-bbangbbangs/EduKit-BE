package com.edukit.external.common.config;

import com.edukit.external.common.handler.CustomAsyncExceptionHandler;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

    private final CustomAsyncExceptionHandler customAsyncExceptionHandler;

    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 3);
        executor.setQueueCapacity(50000);
        executor.setThreadNamePrefix("email-async-");
        executor.setAwaitTerminationSeconds(5);
        executor.setWaitForTasksToCompleteOnShutdown(true);

        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.initialize();
        executor.getThreadPoolExecutor().prestartAllCoreThreads();
        return executor;
    }

    @Bean(name = "aiTaskExecutor")
    public Executor aiTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 3);
        executor.setQueueCapacity(50000);
        executor.setThreadNamePrefix("ai-async-");
        executor.setAwaitTerminationSeconds(5);
        executor.setWaitForTasksToCompleteOnShutdown(true);

        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.initialize();
        executor.getThreadPoolExecutor().prestartAllCoreThreads();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return customAsyncExceptionHandler;
    }

    private static class MdcTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(final Runnable runnable) {
            var contextMap = MDC.getCopyOfContextMap();
            return () -> {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                try {
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        }
    }
}
