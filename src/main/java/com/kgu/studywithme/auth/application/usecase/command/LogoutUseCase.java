package com.kgu.studywithme.auth.application.usecase.command;

public interface LogoutUseCase {
    void logout(final Command command);

    record Command(
            Long memberId
    ) {
    }
}
