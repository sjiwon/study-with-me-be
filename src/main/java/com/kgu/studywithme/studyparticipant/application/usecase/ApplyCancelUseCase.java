package com.kgu.studywithme.studyparticipant.application.usecase;

import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyCancelCommand;
import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ApplyCancelUseCase {
    private final StudyParticipantRepository studyParticipantRepository;

    public void invoke(final ApplyCancelCommand command) {
        final StudyParticipant applier = studyParticipantRepository.getApplier(command.studyId(), command.applierId());
        studyParticipantRepository.delete(applier);
    }
}
