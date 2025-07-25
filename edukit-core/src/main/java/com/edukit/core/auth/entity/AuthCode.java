package com.edukit.core.auth.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.edukit.core.auth.enums.AuthorizeStatus;
import com.edukit.core.auth.enums.AuthCodeType;
import com.edukit.core.member.entity.Member;
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
public class AuthCode {

    @Id
    @Column(name = "auth_code_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String authorizationCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorizeStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthCodeType type;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    private static final int MINUTES_TO_EXPIRE = 10;

    @Builder(access = AccessLevel.PRIVATE)
    private AuthCode(final Member member, final String authorizationCode, final LocalDateTime createdAt,
                     final LocalDateTime expiredAt, final AuthorizeStatus status, final AuthCodeType type) {
        this.member = member;
        this.authorizationCode = authorizationCode;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.status = status;
        this.type = type;
    }

    public static AuthCode create(final Member member, final String authorizationCode,
                                  final AuthorizeStatus status, final AuthCodeType type) {
        return AuthCode.builder()
                .member(member)
                .authorizationCode(authorizationCode)
                .status(status)
                .type(type)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(MINUTES_TO_EXPIRE))
                .build();
    }
}
