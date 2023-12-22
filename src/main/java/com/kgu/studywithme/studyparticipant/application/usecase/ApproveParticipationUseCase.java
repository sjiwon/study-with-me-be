package com.kgu.studywithme.studyparticipant.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApproveParticipationCommand;
import com.kgu.studywithme.studyparticipant.domain.repository.query.ParticipateMemberReader;
import com.kgu.studywithme.studyparticipant.domain.service.ParticipationProcessor;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ApproveParticipationUseCase {
    private final StudyRepository studyRepository;
    private final ParticipateMemberReader participateMemberReader;
    private final ParticipationProcessor participationProcessor;

    @StudyWithMeWritableTransactional
    public void invoke(final ApproveParticipationCommand command) {
        final Study study = studyRepository.getInProgressStudy(command.studyId());
        final Member applier = participateMemberReader.getApplier(command.studyId(), command.applierId());

        participationProcessor.approveApplier(study, applier);
    }
}
