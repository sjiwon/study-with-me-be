package com.kgu.studywithme.studyparticipant.application.usecase.command;

public interface ApplyCancellationUseCase {
    void applyCancellation(final Command command);

    record Command(
            Long studyId,
            Long applierId
    ) {
    }
}
