package com.edukit.core.studentrecord.db.entity;

import com.edukit.core.studentrecord.db.enums.AITaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "student_record_ai_task")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentRecordAITask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_record_ai_task_id")
    private Long id;

    @Column(nullable = false)
    private String prompt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AITaskStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private StudentRecordAITask(final String prompt, final AITaskStatus status, final LocalDateTime startedAt) {
        this.prompt = prompt;
        this.status = status;
        this.startedAt = startedAt;
    }

    public static StudentRecordAITask create(final String prompt) {
        return StudentRecordAITask.builder()
                .prompt(prompt)
                .status(AITaskStatus.PENDING)
                .build();
    }

    public void start() {
        this.status = AITaskStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = AITaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}
