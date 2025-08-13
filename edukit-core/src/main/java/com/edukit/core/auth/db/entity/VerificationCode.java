package com.edukit.core.auth.db.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.edukit.core.auth.db.enums.VerificationCodeType;
import com.edukit.core.auth.db.enums.VerificationStatus;
import com.edukit.core.member.db.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificationCode {

    @Id
    @Column(name = "verification_code_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String verificationCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationCodeType type;

    @Column(nullable = false)
    private int attempts;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    private static final int MINUTES_TO_EXPIRE = 10;
    private static final int INITIAL_ATTEMPT_COUNT = 0;
    private static final int MAX_ATTEMPT_COUNT = 3;

    @Builder(access = AccessLevel.PRIVATE)
    private VerificationCode(final Member member, final String verificationCode, final LocalDateTime createdAt,
                             final LocalDateTime expiredAt, final VerificationStatus status,
                             final VerificationCodeType type, final int attempts) {
        this.member = member;
        this.verificationCode = verificationCode;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.status = status;
        this.type = type;
        this.attempts = attempts;
    }

    public static VerificationCode create(final Member member, final String verificationCode,
                                          final VerificationStatus status, final VerificationCodeType type) {
        return VerificationCode.builder()
                .member(member)
                .verificationCode(verificationCode)
                .status(status)
                .type(type)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(MINUTES_TO_EXPIRE))
                .attempts(INITIAL_ATTEMPT_COUNT)
                .build();
    }

    public void verified() {
        this.status = VerificationStatus.VERIFIED;
    }

    public void expire() {
        this.status = VerificationStatus.EXPIRED;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public boolean isVerificationAttemptLimitExceeded() {
        return attempts >= MAX_ATTEMPT_COUNT;
    }
}
