package com.kgu.studywithme.studyparticipant.application.usecase.command;

public interface ApplyCancellationUseCase {
    void invoke(final Command command);

    record Command(
            Long studyId,
            Long applierId
    ) {
    }
}
