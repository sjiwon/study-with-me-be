package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberReader {
    private final MemberRepository memberRepository;

    public Member getById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public Member getByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
