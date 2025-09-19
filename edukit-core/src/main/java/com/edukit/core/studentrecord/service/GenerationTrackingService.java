package com.edukit.core.studentrecord.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class GenerationTrackingService {

    private final ConcurrentHashMap<Long, GenerationInfo> generationCounts = new ConcurrentHashMap<>();

    public boolean isFirstGeneration(long recordId) {
        GenerationInfo info = generationCounts.compute(recordId, (key, existing) -> {
            if (existing == null) {
                return new GenerationInfo(1, LocalDateTime.now());
            } else {
                existing.incrementCount();
                return existing;
            }
        });

        boolean isFirst = info.getCount() == 1;
        log.debug("RecordId: {}, Generation count: {}, Is first: {}", recordId, info.getCount(), isFirst);

        return isFirst;
    }

    public int getGenerationCount(long recordId) {
        GenerationInfo info = generationCounts.get(recordId);
        return info != null ? info.getCount() : 0;
    }

    public void clearRecord(long recordId) {
        generationCounts.remove(recordId);
        log.debug("Cleared generation tracking for recordId: {}", recordId);
    }

    // 주기적으로 오래된 기록을 정리 (메모리 누수 방지)
    public void cleanupOldRecords() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        generationCounts.entrySet().removeIf(entry ->
            entry.getValue().getFirstGenerationTime().isBefore(cutoff));
    }

    private static class GenerationInfo {
        private final AtomicInteger count;
        private final LocalDateTime firstGenerationTime;

        public GenerationInfo(int initialCount, LocalDateTime firstGenerationTime) {
            this.count = new AtomicInteger(initialCount);
            this.firstGenerationTime = firstGenerationTime;
        }

        public void incrementCount() {
            count.incrementAndGet();
        }

        public int getCount() {
            return count.get();
        }

        public LocalDateTime getFirstGenerationTime() {
            return firstGenerationTime;
        }
    }
}