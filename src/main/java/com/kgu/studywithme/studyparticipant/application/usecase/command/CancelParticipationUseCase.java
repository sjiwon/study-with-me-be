package com.kgu.studywithme.studyparticipant.application.usecase.command;

public interface CancelParticipationUseCase {
    void cancelParticipation(final Command command);

    record Command(
            Long studyId,
            Long participantId
    ) {
    }
}
