package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.usecase.command.UpdateMemberUseCase;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateMemberService implements UpdateMemberUseCase {
    private final QueryMemberByIdService queryMemberByIdService;
    private final MemberRepository memberRepository;

    @Override
    public void update(final Command command) {
        final Member member = queryMemberByIdService.findById(command.memberId());
        validateNicknameIsUnique(command.memberId(), command.nickname());
        validatePhoneIsUnique(command.memberId(), command.phone());

        member.update(
                command.nickname(),
                command.phone(),
                command.province(),
                command.city(),
                command.emailOptIn(),
                command.interests()
        );
    }

    public void validateNicknameIsUnique(
            final Long memberId,
            final String nickname
    ) {
        if (memberRepository.isNicknameUsedByOther(memberId, nickname)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validatePhoneIsUnique(
            final Long memberId,
            final String phone
    ) {
        if (memberRepository.isPhoneUsedByOther(memberId, phone)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }
}
