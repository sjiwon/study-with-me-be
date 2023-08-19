package com.kgu.studywithme.member.infrastructure.persistence;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.adapter.MemberReadAdapter;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberReader implements MemberReadAdapter {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member getById(final Long id) {
        return memberJpaRepository.findById(id)
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    @Override
    public Member getByEmail(final String email) {
        return memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
