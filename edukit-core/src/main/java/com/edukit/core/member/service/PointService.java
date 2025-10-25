package com.edukit.core.member.service;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.db.repository.MemberRepository;
import com.edukit.core.member.exception.MemberErrorCode;
import com.edukit.core.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final MemberRepository memberRepository;

    @Transactional
    public void deductPoints(final Long memberId, final int pointsToDeduct) {
        Member member = memberRepository.findByIdWithLock(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        int currentPoint = member.getPoint();

        if (currentPoint < pointsToDeduct) {
            throw new MemberException(MemberErrorCode.INSUFFICIENT_POINTS);
        }

        member.deductPoints(pointsToDeduct);
    }
}
