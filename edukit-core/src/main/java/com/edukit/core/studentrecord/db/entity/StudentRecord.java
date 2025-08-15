package com.edukit.core.studentrecord.db.entity;

import com.edukit.core.common.domain.BaseTimeEntity;
import com.edukit.core.student.db.entity.Student;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentRecordType studentRecordType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Builder(access = AccessLevel.PRIVATE)
    public StudentRecord(final Student student, final StudentRecordType studentRecordType, final String description) {
        this.student = student;
        this.studentRecordType = studentRecordType;
        this.description = description;
    }

    public static StudentRecord create(final Student student, final StudentRecordType studentRecordType) {
        return StudentRecord.builder()
                .student(student)
                .studentRecordType(studentRecordType)
                .description("")
                .build();
    }

    public void updateDescription(final String description) {
        this.description = description;
    }
}
