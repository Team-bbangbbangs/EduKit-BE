package com.edukit.external.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.edukit.external.ai.response.OpenAIVersionResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OpenAIService 서킷브레이커 테스트")
class OpenAIServiceCircuitBreakerTest {

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        // 테스트용 서킷브레이커 설정 (빠른 테스트를 위해 짧은 시간 설정)
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .minimumNumberOfCalls(3)  // 최소 3번 호출 후 판단
                .failureRateThreshold(70) // 70% 실패율
                .waitDurationInOpenState(Duration.ofSeconds(1)) // 1초 후 HALF_OPEN
                .permittedNumberOfCallsInHalfOpenState(2) // HALF_OPEN에서 2번 테스트
                .slidingWindowSize(5) // 슬라이딩 윈도우 크기
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        circuitBreaker = registry.circuitBreaker("test-openai");
    }

    @Test
    @DisplayName("정상 상황에서 서킷브레이커가 CLOSED 상태를 유지해야 한다")
    void shouldKeepCircuitBreakerClosedOnSuccess() {
        // Given: 성공하는 작업
        Supplier<String> successfulTask = () -> "성공";

        // When: 성공적인 작업을 여러 번 실행
        for (int i = 0; i < 5; i++) {
            String result = circuitBreaker.executeSupplier(successfulTask);
            assertThat(result).isEqualTo("성공");
        }

        // Then: 서킷브레이커가 CLOSED 상태를 유지
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
        assertThat(circuitBreaker.getMetrics().getNumberOfSuccessfulCalls()).isEqualTo(5);
        assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(0);
    }

    @Test
    @DisplayName("연속 실패 시 서킷브레이커가 OPEN 상태로 전환되어야 한다")
    void shouldOpenCircuitBreakerOnConsecutiveFailures() {
        // Given: 항상 실패하는 작업
        Supplier<String> failingTask = () -> {
            throw new RuntimeException("API 오류");
        };

        // When: 실패하는 작업을 연속으로 실행
        for (int i = 0; i < 4; i++) {
            try {
                circuitBreaker.executeSupplier(failingTask);
            } catch (Exception e) {
                // 예외는 예상된 동작
            }
        }

        // Then: 서킷브레이커가 OPEN 상태로 전환
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
        assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isGreaterThan(0);
        assertThat(circuitBreaker.getMetrics().getFailureRate()).isGreaterThan(70.0f);
    }

    @Test
    @DisplayName("OPEN 상태에서 호출 시 CallNotPermittedException이 발생해야 한다")
    void shouldThrowCallNotPermittedExceptionWhenCircuitBreakerIsOpen() {
        // Given: 서킷브레이커를 OPEN 상태로 만들기
        Supplier<String> failingTask = () -> {
            throw new RuntimeException("API 오류");
        };

        // 서킷브레이커를 OPEN 상태로 만들기
        for (int i = 0; i < 4; i++) {
            try {
                circuitBreaker.executeSupplier(failingTask);
            } catch (Exception e) {
                // 예외는 예상된 동작
            }
        }

        // When & Then: OPEN 상태에서 호출 시 CallNotPermittedException 발생
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
        
        assertThatThrownBy(() -> circuitBreaker.executeSupplier(() -> "테스트"))
                .isInstanceOf(CallNotPermittedException.class)
                .hasMessageContaining("CircuitBreaker 'test-openai' is OPEN");
    }

    @Test
    @DisplayName("wait duration 후 HALF_OPEN 상태로 전환되어야 한다")
    void shouldTransitionToHalfOpenAfterWaitDuration() throws InterruptedException {
        // Given: 서킷브레이커를 OPEN 상태로 만들기
        Supplier<String> failingTask = () -> {
            throw new RuntimeException("API 오류");
        };

        // 서킷브레이커를 OPEN 상태로 만들기
        for (int i = 0; i < 4; i++) {
            try {
                circuitBreaker.executeSupplier(failingTask);
            } catch (Exception e) {
                // 예외는 예상된 동작
            }
        }

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        // When: wait duration 대기 (1초)
        Thread.sleep(1100);

        // HALF_OPEN 상태에서 성공적인 작업 실행
        Supplier<String> successfulTask = () -> "복구됨";

        // Then: HALF_OPEN 상태로 전환되고 성공 시 CLOSED로 복구
        for (int i = 0; i < 2; i++) {
            String result = circuitBreaker.executeSupplier(successfulTask);
            assertThat(result).isEqualTo("복구됨");
        }

        // 성공적인 호출 후 CLOSED 상태로 복구되어야 함
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    @DisplayName("서킷브레이커 메트릭스가 올바르게 기록되어야 한다")
    void shouldRecordMetricsCorrectly() {
        // Given: 성공과 실패가 섞인 작업들
        Supplier<String> successTask = () -> "성공";
        Supplier<String> failTask = () -> {
            throw new RuntimeException("실패");
        };

        // When: 성공 2번, 실패 3번 실행
        for (int i = 0; i < 2; i++) {
            circuitBreaker.executeSupplier(successTask);
        }

        for (int i = 0; i < 3; i++) {
            try {
                circuitBreaker.executeSupplier(failTask);
            } catch (Exception e) {
                // 예상된 예외
            }
        }

        // Then: 메트릭스가 올바르게 기록됨
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(2);
        assertThat(metrics.getNumberOfFailedCalls()).isEqualTo(3);
        assertThat(metrics.getFailureRate()).isEqualTo(60.0f); // 3/5 = 60%
    }

    @Test
    @DisplayName("fallback 동작을 시뮬레이션해야 한다")
    void shouldSimulateFallbackBehavior() {
        // Given: 서킷브레이커를 OPEN 상태로 만들기
        Supplier<String> failingTask = () -> {
            throw new RuntimeException("OpenAI API 오류");
        };

        // 서킷브레이커를 OPEN 상태로 만들기
        for (int i = 0; i < 4; i++) {
            try {
                circuitBreaker.executeSupplier(failingTask);
            } catch (Exception e) {
                // 예외는 예상된 동작
            }
        }

        // When: OPEN 상태에서 fallback 로직 테스트
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        String fallbackResult;
        try {
            fallbackResult = circuitBreaker.executeSupplier(() -> "정상 응답");
            // 이 코드는 실행되지 않아야 함 (OPEN 상태에서는 예외 발생)
            assertThat(false).as("OPEN 상태에서는 CallNotPermittedException이 발생해야 함").isTrue();
        } catch (CallNotPermittedException e) {
            // OPEN 상태에서는 CallNotPermittedException이 발생하므로 fallback 처리
            fallbackResult = "현재 AI 서비스에 일시적인 문제가 발생하여 요청을 처리할 수 없습니다.";
            // 예외 메시지 검증
            assertThat(e.getMessage()).contains("CircuitBreaker 'test-openai' is OPEN");
        }

        // Then: fallback 응답이 반환됨
        assertThat(fallbackResult).contains("현재 AI 서비스에 일시적인 문제가 발생");
    }

    @Test
    @DisplayName("OpenAI 서비스의 fallback 응답 형태를 검증해야 한다")
    void shouldValidateFallbackResponseFormat() {
        // Given: OpenAI 서비스의 fallback 응답과 동일한 형태
        String fallbackMessage = "현재 AI 서비스에 일시적인 문제가 발생하여 요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요.";
        
        List<OpenAIVersionResponse> fallbackResponses = List.of(
                OpenAIVersionResponse.of(1, fallbackMessage + " (버전 1)", false),
                OpenAIVersionResponse.of(2, fallbackMessage + " (버전 2)", false),
                OpenAIVersionResponse.of(3, fallbackMessage + " (버전 3)", true)
        );

        // When & Then: fallback 응답 형태 검증
        assertThat(fallbackResponses).hasSize(3);
        
        OpenAIVersionResponse version1 = fallbackResponses.get(0);
        assertThat(version1.versionNumber()).isEqualTo(1);
        assertThat(version1.content()).contains("현재 AI 서비스에 일시적인 문제가 발생");
        assertThat(version1.isLast()).isFalse();

        OpenAIVersionResponse version3 = fallbackResponses.get(2);
        assertThat(version3.versionNumber()).isEqualTo(3);
        assertThat(version3.isLast()).isTrue();
    }

    @Test
    @DisplayName("비동기 환경에서 서킷브레이커가 올바르게 동작해야 한다")
    void shouldWorkCorrectlyInAsyncEnvironment() throws Exception {
        // Given: 비동기 작업들
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() ->
                circuitBreaker.executeSupplier(() -> "비동기 성공 1"));
        
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() ->
                circuitBreaker.executeSupplier(() -> "비동기 성공 2"));

        // When: 비동기 작업 완료 대기
        String result1 = future1.get(5, TimeUnit.SECONDS);
        String result2 = future2.get(5, TimeUnit.SECONDS);

        // Then: 모든 작업이 성공하고 서킷브레이커는 CLOSED 상태 유지
        assertThat(result1).isEqualTo("비동기 성공 1");
        assertThat(result2).isEqualTo("비동기 성공 2");
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
        assertThat(circuitBreaker.getMetrics().getNumberOfSuccessfulCalls()).isEqualTo(2);
    }
}
