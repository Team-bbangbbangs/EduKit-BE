package com.edukit.core.member.service;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.db.enums.MemberRole;
import com.edukit.core.member.db.enums.School;
import com.edukit.core.member.db.repository.MemberRepository;
import com.edukit.core.member.db.repository.NicknameBannedWordRepository;
import com.edukit.core.member.exception.MemberErrorCode;
import com.edukit.core.member.exception.MemberException;
import com.edukit.core.subject.db.entity.Subject;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final NicknameBannedWordRepository nicknameBannedWordRepository;

    private static final boolean DELETED = true;
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]{2,16}$");

    @Transactional(readOnly = true)
    public Member getMemberByUuid(final String memberUuid) {
        return memberRepository.findByMemberUuidAndIsDeleted(memberUuid, false)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Member getMemberById(final long memberId) {
        return memberRepository.findByIdAndIsDeleted(memberId, false)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Member getMemberWithSubjectById(final long memberId) {
        return memberRepository.findByIdAndIsDeletedFetchJoinSubject(memberId, false)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional
    public Member createMember(final String email, final String encodedPassword, final Subject subject,
                               final String nickname, final School school) {
        try {
            return saveMember(email, encodedPassword, subject, nickname, school);
        } catch (DataIntegrityViolationException e) {
            throw new MemberException(MemberErrorCode.MEMBER_ALREADY_REGISTERED);
        }
    }

    @Transactional
    public void updateMemberProfileAndFlush(final Member member, final Subject subject, final School school, final String nickname) {
        validateNickname(nickname, member);
        member.updateProfile(subject, school, nickname);
        memberRepository.flush();
    }

    public void validateNickname(final String nickname, final Member member) {
        validateNicknameInvalid(nickname);
        validateNicknameDuplicated(nickname, member);
    }

    public void validateNickname(final String nickname) {
        validateNicknameInvalid(nickname);
        validateNicknameDuplicated(nickname);
    }

    private void validateNicknameInvalid(final String nickname) {
        if (isNicknameInvalid(nickname)) {
            throw new MemberException(MemberErrorCode.INVALID_NICKNAME);
        }
    }

    private void validateNicknameDuplicated(final String nickname, final Member member) {
        if (isNicknameDuplicated(nickname, member)) {
            throw new MemberException(MemberErrorCode.DUPLICATED_NICKNAME);
        }
    }

    private void validateNicknameDuplicated(final String nickname) {
        if (isNicknameDuplicated(nickname)) {
            throw new MemberException(MemberErrorCode.DUPLICATED_NICKNAME);
        }
    }

    public boolean isNicknameInvalid(final String nickname) {
        return nickname.isBlank()
                || !NICKNAME_PATTERN.matcher(nickname).matches()
                || nicknameBannedWordRepository.existsBannedWordIn(nickname);
    }

    public boolean isNicknameDuplicated(final String nickname, final Member member) {
        if (member.getNickname().equals(nickname)) {
            return true;
        }
        return isNicknameDuplicated(nickname);
    }

    private boolean isNicknameDuplicated(final String nickname) {
        return memberRepository.existsByNicknameIgnoreCase(nickname);
    }

    @Transactional
    public void withdraw(final Member member) {
        member.withdraw();
    }

    private Member saveMember(final String email, final String encodedPassword, final Subject subject,
                              final String nickname, final School school) {
        Optional<Member> restored = restoreIfSoftDeletedMemberByEmail(email, encodedPassword, subject, nickname,
                school);
        if (restored.isPresent()) { // 재가입 회원 -> 복구 처리
            return restored.get();
        }

        // 새로운 회원 가입
        Member newMember = Member.create(subject, email, encodedPassword, nickname, school, MemberRole.PENDING_TEACHER);
        memberRepository.save(newMember);
        return newMember;
    }

    private Optional<Member> restoreIfSoftDeletedMemberByEmail(final String email, final String password,
                                                               final Subject subject, final String nickname,
                                                               final School school) {
        return memberRepository.findByEmailAndIsDeleted(email, DELETED)
                .map(member -> {
                    member.restore(password, subject, nickname, school);
                    memberRepository.flush();
                    return member;
                });
    }

    @Transactional(readOnly = true)
    public Member getMemberByEmail(final String email) {
        return memberRepository.findByEmailAndIsDeleted(email, false)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional
    public void updatePassword(final Member member, final String encodedPassword) {
        member.updatePassword(encodedPassword);
    }

    public void memberVerified(final Member member) {
        member.verifyAsTeacher();
    }
}
