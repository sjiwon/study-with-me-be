package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyCancellationUseCase;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class ApplyCancellationService implements ApplyCancellationUseCase {
    private final StudyParticipantRepository studyParticipantRepository;

    @Override
    public void invoke(final Command command) {
        validateRequesterIsApplier(command.studyId(), command.applierId());
        studyParticipantRepository.deleteApplier(command.studyId(), command.applierId());
    }

    private void validateRequesterIsApplier(final Long studyId, final Long applierId) {
        if (!studyParticipantRepository.isApplier(studyId, applierId)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND);
        }
    }
}
