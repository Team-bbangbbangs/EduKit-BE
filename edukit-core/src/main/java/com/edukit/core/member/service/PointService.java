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

    private static final int MINIMUM_REQUIRED_POINTS = 100;

    @Transactional(readOnly = true)
    public void checkSufficientPoints(final Member member) {
        int point = member.getPoint();

        if (point < MINIMUM_REQUIRED_POINTS) {
            throw new MemberException(MemberErrorCode.INSUFFICIENT_POINTS);
        }
    }
}
