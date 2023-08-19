package com.kgu.studywithme.studyparticipant.application.usecase.command;

public interface ApproveParticipationUseCase {
    void invoke(final Command command);

    record Command(
            Long studyId,
            Long applierId
    ) {
    }
}
