package com.kgu.studywithme.studyparticipant.application.usecase;

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
    private final ParticipationInspector participationInspector;
    private final StudyParticipantRepository studyParticipantRepository;

    public Long invoke(final ApplyStudyCommand command) {
        final Study study = studyRepository.getRecruitingStudy(command.studyId());
        participationInspector.checkApplierIsHost(study, command.applierId());
        participationInspector.checkApplierIsAlreadyRelatedToStudy(study, command.applierId());

        return studyParticipantRepository.save(command.toDomain()).getId();
    }
}
