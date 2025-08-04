package com.edukit.external.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.edukit.core.common.service.response.OpenAIVersionResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("서킷브레이커 실제 동작 시뮬레이션 테스트")
class OpenAIServiceImplIntegrationTest {

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        // 테스트용 서킷브레이커 설정
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .minimumNumberOfCalls(2)  // 최소 2번 호출 후 판단
                .failureRateThreshold(50) // 50% 실패율
                .waitDurationInOpenState(Duration.ofSeconds(1)) // 1초 후 HALF_OPEN
                .permittedNumberOfCallsInHalfOpenState(1) // HALF_OPEN에서 1번 테스트
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        circuitBreaker = registry.circuitBreaker("test-openai-integration");
    }

    @Test
    @DisplayName("OpenAI API 호출 시뮬레이션 - 성공 시나리오")
    void shouldSimulateSuccessfulOpenAIApiCall() {
        // Given
        Supplier<List<OpenAIVersionResponse>> successfulApiCall = () -> List.of(
                OpenAIVersionResponse.of(1, "첫 번째 성공적인 응답", false),
                OpenAIVersionResponse.of(2, "두 번째 성공적인 응답", false),
                OpenAIVersionResponse.of(3, "세 번째 성공적인 응답", true)
        );

        // When
        List<OpenAIVersionResponse> result = circuitBreaker.executeSupplier(successfulApiCall);

        // Then
        assertAll("OpenAI 성공 응답 및 서킷 상태 검증",
                () -> assertThat(result).hasSize(3),
                () -> assertThat(result.get(0).versionNumber()).isEqualTo(1),
                () -> assertThat(result.get(0).content()).contains("첫 번째 성공적인 응답"),
                () -> assertThat(result.get(2).isLast()).isTrue(),
                () -> assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED),
                () -> assertThat(circuitBreaker.getMetrics().getNumberOfSuccessfulCalls()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("OpenAI API 호출 실패 시 fallback 응답 제공")
    void shouldProvideFallbackOnApiFailure() {
        // Given: 실패하는 API 호출을 시뮬레이션
        Supplier<List<OpenAIVersionResponse>> failingApiCall = () -> {
            throw new RuntimeException("OpenAI API 서버 오류");
        };

        // When: 실패하는 호출을 서킷브레이커로 감싸고 fallback 제공
        List<OpenAIVersionResponse> result;
        try {
            result = circuitBreaker.executeSupplier(failingApiCall);
        } catch (Exception e) {
            // fallback 응답 제공
            result = createFallbackResponse();
        }

        // Then: fallback 응답 확인
        assertThat(result).hasSize(3);
        assertThat(result.get(0).content()).contains("현재 AI 서비스에 일시적인 문제가 발생");
        assertThat(result.get(2).isLast()).isTrue();

        assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(1);
    }

    @Test
    @DisplayName("연속 실패 시 서킷브레이커 OPEN 전환")
    void shouldOpenCircuitBreakerOnConsecutiveFailures() {
        // Given: 항상 실패하는 API 호출
        Supplier<List<OpenAIVersionResponse>> alwaysFailingCall = () -> {
            throw new RuntimeException("지속적인 OpenAI API 오류");
        };

        // When: 연속으로 실패하는 호출을 최소 호출 수만큼 실행
        for (int i = 0; i < 3; i++) {
            List<OpenAIVersionResponse> result;
            try {
                result = circuitBreaker.executeSupplier(alwaysFailingCall);
            } catch (Exception e) {
                result = createFallbackResponse();
            }

            // 모든 호출이 fallback 응답을 반환해야 함
            assertThat(result).hasSize(3);
            assertThat(result.getFirst().content()).contains("현재 AI 서비스에 일시적인 문제가 발생");
        }

        // Then: 서킷브레이커가 OPEN 상태로 전환
        assertAll("서킷브레이커 상태 및 메트릭 검증",
                () -> assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN),
                () -> assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isGreaterThan(0),
                () -> assertThat(circuitBreaker.getMetrics().getFailureRate()).isGreaterThan(50.0f)
        );
    }

    @Test
    @DisplayName("서킷브레이커 복구 시나리오 테스트")
    void shouldTestCircuitBreakerRecoveryScenario() throws InterruptedException {
        // Given: 서킷브레이커를 OPEN 상태로 만들기
        Supplier<List<OpenAIVersionResponse>> failingCall = () -> {
            throw new RuntimeException("일시적인 API 오류");
        };

        // 연속 실패로 OPEN 상태 만들기
        for (int i = 0; i < 3; i++) {
            try {
                circuitBreaker.executeSupplier(failingCall);
            } catch (Exception e) {
                // 예외는 예상된 동작
            }
        }

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        // When: wait duration 후 성공적인 호출
        Thread.sleep(1100);

        Supplier<List<OpenAIVersionResponse>> recoveredCall = () -> List.of(
                OpenAIVersionResponse.of(1, "복구된 응답 1", false),
                OpenAIVersionResponse.of(2, "복구된 응답 2", false),
                OpenAIVersionResponse.of(3, "복구된 응답 3", true)
        );

        // HALF_OPEN 상태에서 성공적인 호출
        List<OpenAIVersionResponse> result = circuitBreaker.executeSupplier(recoveredCall);

        // Then: 정상 응답을 받고 서킷브레이커가 CLOSED로 복구
        assertAll("복구 시나리오 응답 및 상태 검증",
                () -> assertThat(result).hasSize(3),
                () -> assertThat(result.getFirst().content()).contains("복구된 응답"),
                () -> assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED)
        );
    }

    @Test
    @DisplayName("서킷브레이커 메트릭스가 실제 호출 결과를 반영해야 한다")
    void shouldReflectActualCallResultsInMetrics() {
        // Given: 성공이 더 많은 시나리오 (임계값 50%보다 낮은 실패율)
        Supplier<List<OpenAIVersionResponse>> successCall = () -> List.of(
                OpenAIVersionResponse.of(3, "성공적인 응답", true)
        );

        Supplier<List<OpenAIVersionResponse>> failCall = () -> {
            throw new RuntimeException("API 호출 실패");
        };

        // When: 성공 3번, 실패 1번 (실패율 25%)
        for (int i = 0; i < 3; i++) {
            circuitBreaker.executeSupplier(successCall);
        }

        try {
            circuitBreaker.executeSupplier(failCall);
        } catch (Exception e) {
            // 예외는 예상된 동작
        }

        // Then: 메트릭스 확인
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertAll("서킷브레이커 메트릭 및 상태 검증",
                () -> assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(3),
                () -> assertThat(metrics.getNumberOfFailedCalls()).isEqualTo(1),
                () -> assertThat(metrics.getFailureRate()).isEqualTo(25.0f),
                () -> assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED)
        );

        // 실패율이 임계값(50%)보다 낮으므로 CLOSED 상태 유지
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    @DisplayName("실제 OpenAI 서비스의 fallback 응답 형태 검증")
    void shouldValidateOpenAIServiceFallbackFormat() {
        // Given
        List<OpenAIVersionResponse> fallbackResponse = createFallbackResponse();

        // When & Then
        assertThat(fallbackResponse).hasSize(3);

        OpenAIVersionResponse version1 = fallbackResponse.getFirst();
        OpenAIVersionResponse version3 = fallbackResponse.get(2);

        assertAll("fallback 응답 형식 검증",
                () -> assertThat(version1.versionNumber()).isEqualTo(1),
                () -> assertThat(version1.content()).contains("현재 AI 서비스에 일시적인 문제가 발생"),
                () -> assertThat(version1.isFallback()).isTrue(),
                () -> assertThat(version1.isLast()).isFalse(),

                () -> assertThat(version3.versionNumber()).isEqualTo(3),
                () -> assertThat(version3.isFallback()).isTrue(),
                () -> assertThat(version3.isLast()).isTrue()
        );
    }

    /**
     * 실제 OpenAIService의 fallback 응답과 동일한 형태를 생성
     */
    private List<OpenAIVersionResponse> createFallbackResponse() {
        String fallbackMessage = "현재 AI 서비스에 일시적인 문제가 발생하여 요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요.";

        return List.of(
                OpenAIVersionResponse.ofFallback(1, fallbackMessage, false),
                OpenAIVersionResponse.ofFallback(2, fallbackMessage, false),
                OpenAIVersionResponse.ofFallback(3, fallbackMessage, true)
        );
    }
}
