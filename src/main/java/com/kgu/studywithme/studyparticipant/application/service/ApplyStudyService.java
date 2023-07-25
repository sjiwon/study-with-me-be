package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.application.service.QueryStudyByIdService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyStudyUseCase;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class ApplyStudyService implements ApplyStudyUseCase {
    private final QueryStudyByIdService queryStudyByIdService;
    private final StudyParticipantRepository studyParticipantRepository;

    @Override
    public void apply(final Command command) {
        final Study study = queryStudyByIdService.findById(command.studyId());
        validateStudyIsRecruiting(study);
        validateApplierIsHost(study, command.applierId());
        validateApplierIsAlreadyRelatedToStudy(study, command.applierId());

        final StudyParticipant participant = StudyParticipant.applyInStudy(command.studyId(), command.applierId());
        studyParticipantRepository.save(participant);
    }

    private void validateStudyIsRecruiting(final Study study) {
        if (study.isRecruitmentComplete()) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.STUDY_IS_NOT_RECRUITING_NOW);
        }
    }

    private void validateApplierIsHost(final Study study, final Long applierId) {
        if (study.isHost(applierId)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.STUDY_HOST_CANNOT_APPLY);
        }
    }

    private void validateApplierIsAlreadyRelatedToStudy(final Study study, final Long applierId) {
        if (studyParticipantRepository.isApplierOrParticipant(study.getId(), applierId)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.ALREADY_APPLY_OR_PARTICIPATE);
        }

        if (studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), applierId)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.ALREADY_LEAVE_OR_GRADUATED);
        }
    }
}
