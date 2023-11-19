package com.kgu.studywithme.studyparticipant.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyStudyCommand;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.domain.service.ParticipationInspector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplyStudyUseCase {
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final ParticipationInspector participationInspector;
    private final StudyParticipantRepository studyParticipantRepository;

    @StudyWithMeWritableTransactional
    public Long invoke(final ApplyStudyCommand command) {
        final Study study = studyRepository.getRecruitingStudy(command.studyId());
        final Member applier = memberRepository.getById(command.applierId());

        participationInspector.checkApplierIsHost(study, applier);
        participationInspector.checkApplierIsAlreadyRelatedToStudy(study, applier);
        return studyParticipantRepository.save(command.toDomain()).getId();
    }
}
