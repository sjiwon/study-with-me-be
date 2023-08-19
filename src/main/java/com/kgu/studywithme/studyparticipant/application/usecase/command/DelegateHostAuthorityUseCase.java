package com.kgu.studywithme.studyparticipant.application.usecase.command;

public interface DelegateHostAuthorityUseCase {
    void invoke(final Command command);

    record Command(
            Long studyId,
            Long newHostId
    ) {
    }
}
