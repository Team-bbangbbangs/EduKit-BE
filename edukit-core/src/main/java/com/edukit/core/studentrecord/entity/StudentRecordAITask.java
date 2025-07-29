package com.edukit.core.studentrecord.entity;

import com.edukit.core.studentrecord.enums.AITaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_record_id", nullable = false)
    private StudentRecord studentRecord;

    @Column(nullable = false)
    private String prompt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AITaskStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private StudentRecordAITask(final StudentRecord studentRecord, final String prompt, final AITaskStatus status,
                                final LocalDateTime startedAt) {
        this.studentRecord = studentRecord;
        this.prompt = prompt;
        this.status = status;
        this.startedAt = startedAt;
    }

    public static StudentRecordAITask create(final StudentRecord studentRecord, final String prompt) {
        return StudentRecordAITask.builder()
                .studentRecord(studentRecord)
                .prompt(prompt)
                .status(AITaskStatus.PENDING)
                .startedAt(LocalDateTime.now())
                .build();
    }
}
