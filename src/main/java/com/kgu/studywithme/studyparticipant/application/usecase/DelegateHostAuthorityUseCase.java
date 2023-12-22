package com.kgu.studywithme.studyparticipant.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyparticipant.application.usecase.command.DelegateHostAuthorityCommand;
import com.kgu.studywithme.studyparticipant.domain.repository.query.ParticipateMemberReader;
import com.kgu.studywithme.studyparticipant.domain.service.ParticipationInspector;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class DelegateHostAuthorityUseCase {
    private final StudyRepository studyRepository;
    private final ParticipateMemberReader participateMemberReader;
    private final ParticipationInspector participationInspector;

    @StudyWithMeWritableTransactional
    public void invoke(final DelegateHostAuthorityCommand command) {
        final Study study = studyRepository.getInProgressStudy(command.studyId());
        final Member participant = participateMemberReader.getParticipant(command.studyId(), command.newHostId());

        participationInspector.checkNewHostIsCurrentHost(study, participant);
        study.delegateHostAuthority(participant);
    }
}
