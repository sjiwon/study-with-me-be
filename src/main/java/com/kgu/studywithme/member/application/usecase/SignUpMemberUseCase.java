package com.kgu.studywithme.member.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.member.application.usecase.command.SignUpMemberCommand;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.member.domain.service.MemberResourceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpMemberUseCase {
    private final MemberResourceValidator memberResourceValidator;
    private final MemberRepository memberRepository;

    @StudyWithMeWritableTransactional
    public Long invoke(final SignUpMemberCommand command) {
        memberResourceValidator.validateInSignUp(command.email(), command.nickname(), command.phone());
        return memberRepository.save(command.toDomain()).getId();
    }
}
