package com.kgu.studywithme.auth.application.usecase.command;

public interface LogoutUseCase {
    void logout(Command command);

    record Command(
            Long memberId
    ) {
    }
}
