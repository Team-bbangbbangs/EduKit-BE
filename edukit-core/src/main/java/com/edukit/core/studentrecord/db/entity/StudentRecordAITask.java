package com.edukit.core.studentrecord.db.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.studentrecord.db.enums.AIErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String prompt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private LocalDateTime failedAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private AIErrorType errorType;

    @Builder(access = AccessLevel.PRIVATE)
    private StudentRecordAITask(final Member member, final String prompt, final LocalDateTime startedAt) {
        this.member = member;
        this.prompt = prompt;
        this.startedAt = startedAt;
    }

    public static StudentRecordAITask create(final Member member, final String prompt) {
        return StudentRecordAITask.builder()
                .member(member)
                .prompt(prompt)
                .build();
    }

    public void start() {
        this.startedAt = LocalDateTime.now();
    }

    public void complete() {
        this.completedAt = LocalDateTime.now();
    }

    public void markAsFailed(final AIErrorType errorType) {
        this.failedAt = LocalDateTime.now();
        this.errorType = errorType;
    }
}
