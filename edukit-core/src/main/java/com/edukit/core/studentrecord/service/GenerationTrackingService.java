package com.edukit.core.studentrecord.service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public void clearRecord(long recordId) {
        generationCounts.remove(recordId);
        log.debug("Cleared generation tracking for recordId: {}", recordId);
    }

    private static class GenerationInfo {
        private final AtomicInteger count;
        @Getter
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

    }
}
