package com.kgu.studywithme.auth.application.service;

import com.kgu.studywithme.auth.application.adapter.TokenPersistenceAdapter;
import com.kgu.studywithme.auth.application.usecase.command.LogoutUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutUseCase {
    private final TokenPersistenceAdapter tokenPersistenceAdapter;

    @Override
    public void invoke(final Command command) {
        tokenPersistenceAdapter.deleteMemberRefreshToken(command.memberId());
    }
}
