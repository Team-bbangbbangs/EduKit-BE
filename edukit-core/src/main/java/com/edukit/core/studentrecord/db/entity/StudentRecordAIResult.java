package com.edukit.core.studentrecord.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "student_record_ai_result")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentRecordAIResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_record_ai_result_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_record_ai_task_id", nullable = false)
    private StudentRecordAITask studentRecordAITask;

    @Column(nullable = false)
    private String result;

    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private StudentRecordAIResult(final StudentRecordAITask studentRecordAITask, final String result,
                                  final LocalDateTime createdAt) {
        this.studentRecordAITask = studentRecordAITask;
        this.result = result;
        this.createdAt = createdAt;
    }

    public static StudentRecordAIResult create(final StudentRecordAITask studentRecordAITask, final String result) {
        return StudentRecordAIResult.builder()
                .studentRecordAITask(studentRecordAITask)
                .result(result)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
