package com.kgu.studywithme.studyparticipant.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyparticipant.application.usecase.command.GraduateStudyCommand;
import com.kgu.studywithme.studyparticipant.domain.repository.query.ParticipateMemberReader;
import com.kgu.studywithme.studyparticipant.domain.service.ParticipationInspector;
import com.kgu.studywithme.studyparticipant.domain.service.ParticipationProcessor;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class GraduateStudyUseCase {
    private final StudyRepository studyRepository;
    private final ParticipateMemberReader participateMemberReader;
    private final ParticipationInspector participationInspector;
    private final ParticipationProcessor participationProcessor;

    @StudyWithMeWritableTransactional
    public void invoke(final GraduateStudyCommand command) {
        final Study study = studyRepository.getByIdWithHost(command.studyId());
        final Member participant = participateMemberReader.getParticipant(command.studyId(), command.participantId());

        participationInspector.checkGraduationCandidateIsHost(study, participant);
        participationInspector.checkGraduationCandidateMeetGraduationPolicy(study, participant);
        participationProcessor.graduateStudy(study, participant);
    }
}
