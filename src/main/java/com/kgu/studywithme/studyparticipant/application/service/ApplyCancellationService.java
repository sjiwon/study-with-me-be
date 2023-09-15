package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyCancellationUseCase;
import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPLY;

@Service
@RequiredArgsConstructor
public class ApplyCancellationService implements ApplyCancellationUseCase {
    private final StudyParticipantRepository studyParticipantRepository;

    @Override
    public void invoke(final Command command) {
        final StudyParticipant applier = getApplier(command.studyId(), command.applierId());
        studyParticipantRepository.delete(applier);
    }

    private StudyParticipant getApplier(final Long studyId, final Long applierId) {
        return studyParticipantRepository.findParticipantByStatus(studyId, applierId, APPLY)
                .orElseThrow(() -> StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND));
    }
}
