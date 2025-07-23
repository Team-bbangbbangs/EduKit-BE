package com.edukit.studentrecord.entity;

import com.edukit.common.domain.BaseTimeEntity;
import com.edukit.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Student extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String classNumber;

    @Column(nullable = false)
    private String studentNumber;

    @Column(nullable = false)
    private String studentName;

    @Builder(access = AccessLevel.PRIVATE)
    public Student(Member member, String grade, String classNumber, String studentNumber, String studentName) {
        this.member = member;
        this.grade = grade;
        this.classNumber = classNumber;
        this.studentNumber = studentNumber;
        this.studentName = studentName;
    }
}
