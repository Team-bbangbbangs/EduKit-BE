package com.edukit.core.member.db.entity;

import com.edukit.core.common.domain.BaseTimeEntity;
import com.edukit.core.member.db.enums.MemberRole;
import com.edukit.core.member.db.enums.School;
import com.edukit.core.subject.db.entity.Subject;
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
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String memberUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private School school;

    @Column(length = 500)
    private String profileImageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column
    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    private boolean isDeleted;

    private LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Member(final Subject subject, final String email, final String password, final String nickname,
                   final School school, final MemberRole role, final LocalDateTime verifiedAt, final boolean isDeleted,
                   final LocalDateTime deletedAt, final String memberUuid) {
        this.subject = subject;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.school = school;
        this.role = role;
        this.verifiedAt = verifiedAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.memberUuid = memberUuid;
    }

    public static Member create(final Subject subject, final String email, final String password,
                                final String nickname, final School school, final MemberRole memberRole) {
        return Member.builder()
                .subject(subject)
                .email(email)
                .password(password)
                .nickname(nickname)
                .school(school)
                .role(memberRole)
                .isDeleted(false)
                .memberUuid(UUID.randomUUID().toString())
                .build();
    }

    public void restore(final String password, final Subject subject, final String nickname, final School school) {
        this.isDeleted = false;
        this.deletedAt = null;
        this.password = password;
        this.subject = subject;
        this.nickname = nickname;
        this.school = school;
        this.role = MemberRole.PENDING_TEACHER;
        this.verifiedAt = null;
    }

    public boolean isAdmin() {
        return this.getRole() == MemberRole.ADMIN;
    }

    public void updatePassword(final String password) {
        this.password = password;
    }

    public boolean isVerifyTeacher() {
        return this.role != MemberRole.PENDING_TEACHER;
    }

    public void updateProfile(final Subject subject, final School school, final String nickname) {
        this.subject = subject;
        this.school = school;
        this.nickname = nickname;
    }

    public void withdraw() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void verifyAsTeacher() {
        this.role = MemberRole.TEACHER;
        this.verifiedAt = LocalDateTime.now();
    }

    public void updateEmailAndChangeVerifyStatus(final String email) {
        this.email = email;
        this.role = MemberRole.PENDING_TEACHER;
    }
}
