package com.kgu.studywithme.member.application;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberFindService {
    private final MemberRepository memberRepository;

    public Member findById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public Member findByIdWithInterests(final Long memberId) {
        return memberRepository.findByIdWithInterests(memberId)
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public Member findByEmail(final String email) {
        return memberRepository.findByEmail(Email.from(email))
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
