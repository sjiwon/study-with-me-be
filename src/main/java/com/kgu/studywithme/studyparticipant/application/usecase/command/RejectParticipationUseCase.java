package com.kgu.studywithme.studyparticipant.application.usecase.command;

public interface RejectParticipationUseCase {
    void rejectParticipation(final Command command);

    record Command(
            Long studyId,
            Long applierId,
            String reason
    ) {
    }
}