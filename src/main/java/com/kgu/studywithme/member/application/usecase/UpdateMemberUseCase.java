package com.kgu.studywithme.member.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.member.application.usecase.command.UpdateMemberCommand;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.member.domain.service.MemberResourceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateMemberUseCase {
    private final MemberResourceValidator memberResourceValidator;
    private final MemberRepository memberRepository;

    public void invoke(final UpdateMemberCommand command) {
        memberResourceValidator.validateInUpdate(command.memberId(), command.nickname(), command.phone());

        final Member member = memberRepository.getById(command.memberId());
        member.update(
                command.nickname(),
                command.phone(),
                command.address(),
                command.emailOptIn(),
                command.interests()
        );
    }
}
