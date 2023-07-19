package com.kgu.studywithme.studyparticipant.application.usecase.command;

public interface LeaveParticipationUseCase {
    void leaveParticipation(final Command command);

    record Command(
            Long studyId,
            Long participantId
    ) {
    }
}
