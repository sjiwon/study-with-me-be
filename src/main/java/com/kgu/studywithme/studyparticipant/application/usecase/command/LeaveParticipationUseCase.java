package com.kgu.studywithme.studyparticipant.application.usecase.command;

public interface LeaveParticipationUseCase {
    void invoke(final Command command);

    record Command(
            Long studyId,
            Long participantId
    ) {
    }
}
