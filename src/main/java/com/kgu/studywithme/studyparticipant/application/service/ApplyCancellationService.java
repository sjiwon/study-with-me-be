package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyCancellationUseCase;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import com.kgu.studywithme.studyparticipant.infrastructure.persistence.StudyParticipantJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPLY;

@Service
@RequiredArgsConstructor
public class ApplyCancellationService implements ApplyCancellationUseCase {
    private final StudyParticipantJpaRepository studyParticipantJpaRepository;

    @Override
    public void invoke(final Command command) {
        final StudyParticipant applier = getApplier(command.studyId(), command.applierId());
        studyParticipantJpaRepository.delete(applier);
    }

    private StudyParticipant getApplier(final Long studyId, final Long applierId) {
        return studyParticipantJpaRepository.findParticipantByStatus(studyId, applierId, APPLY)
                .orElseThrow(() -> StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND));
    }
}
