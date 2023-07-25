package com.kgu.studywithme.studyparticipant.application.usecase.command;

public interface DelegateHostAuthorityUseCase {
    void delegateHostAuthority(final Command command);

    record Command(
            Long studyId,
            Long newHostId
    ) {
    }
}
