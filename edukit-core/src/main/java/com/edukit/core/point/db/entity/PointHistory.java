package com.edukit.core.point.db.entity;

import com.edukit.core.common.domain.BaseTimeEntity;
import com.edukit.core.member.db.entity.Member;
import com.edukit.core.point.db.enums.PointTransactionType;
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
public class PointHistory extends BaseTimeEntity {

    @Id
    @Column(name = "point_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointTransactionType transactionType;

    @Column(nullable = false)
    private int amount;

    @Column
    private Long taskId;

    @Builder(access = AccessLevel.PRIVATE)
    private PointHistory(final Member member, final PointTransactionType transactionType,
                         final int amount, final Long taskId) {
        this.member = member;
        this.transactionType = transactionType;
        this.amount = amount;
        this.taskId = taskId;
    }

    public static PointHistory create(final Member member, final PointTransactionType transactionType,
                                      final int amount, final Long taskId) {
        return PointHistory.builder()
                .member(member)
                .transactionType(transactionType)
                .amount(amount)
                .taskId(taskId)
                .build();
    }
}
