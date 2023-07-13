package com.kgu.studywithme.auth.application.service;

import com.kgu.studywithme.auth.application.usecase.command.LogoutUseCase;
import com.kgu.studywithme.auth.infrastructure.token.TokenPersistenceAdapter;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class LogoutService implements LogoutUseCase {
    private final TokenPersistenceAdapter tokenPersistenceAdapter;

    @Override
    public void logout(Command command) {
        tokenPersistenceAdapter.deleteRefreshTokenByMemberId(command.memberId());
    }
}
