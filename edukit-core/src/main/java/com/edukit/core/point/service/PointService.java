package com.edukit.core.point.service;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.db.repository.MemberRepository;
import com.edukit.core.member.exception.MemberErrorCode;
import com.edukit.core.member.exception.MemberException;
import com.edukit.core.point.db.entity.PointHistory;
import com.edukit.core.point.db.enums.PointTransactionType;
import com.edukit.core.point.db.repository.PointHistoryRepository;
import com.edukit.core.point.exception.PointErrorCode;
import com.edukit.core.point.exception.PointException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public void deductPoints(final Long memberId, final int pointsToDeduct, final Long taskId) {
        Member member = memberRepository.findByIdWithLock(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        int currentPoint = member.getPoint();

        if (currentPoint < pointsToDeduct) {
            throw new PointException(PointErrorCode.INSUFFICIENT_POINTS);
        }

        member.deductPoints(pointsToDeduct);

        PointHistory history = PointHistory.create(member, PointTransactionType.DEDUCT, pointsToDeduct, taskId);
        pointHistoryRepository.save(history);
    }

    @Transactional
    public void compensatePoints(final Long memberId, final int pointsToCompensate, final Long taskId) {
        Member member = memberRepository.findByIdWithLock(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.addPoints(pointsToCompensate); // 음수 차감으로 복구

        // 포인트 히스토리 기록
        PointHistory history = PointHistory.create(member, PointTransactionType.COMPENSATION, pointsToCompensate, taskId);
        pointHistoryRepository.save(history);
    }
}
