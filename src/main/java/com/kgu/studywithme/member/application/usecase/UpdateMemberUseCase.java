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
    private final MemberRepository memberRepository;
    private final MemberResourceValidator memberResourceValidator;

    public void invoke(final UpdateMemberCommand command) {
        final Member member = memberRepository.getById(command.memberId());
        memberResourceValidator.validateInUpdate(member);

        member.update(
                command.nickname(),
                command.phone(),
                command.address(),
                command.emailOptIn(),
                command.interests()
        );
    }
}
