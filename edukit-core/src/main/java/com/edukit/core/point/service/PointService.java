package com.edukit.core.point.service;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.db.repository.MemberRepository;
import com.edukit.core.member.exception.MemberErrorCode;
import com.edukit.core.member.exception.MemberException;
import com.edukit.core.point.exception.PointErrorCode;
import com.edukit.core.point.exception.PointException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member deductPoints(final Long memberId, final int pointsToDeduct) {
        Member member = memberRepository.findByIdWithLock(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        int currentPoint = member.getPoint();

        if (currentPoint < pointsToDeduct) {
            throw new PointException(PointErrorCode.INSUFFICIENT_POINTS);
        }

        member.deductPoints(pointsToDeduct);
        return member;
    }
}
