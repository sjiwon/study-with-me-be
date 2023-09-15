package com.kgu.studywithme.auth.application.usecase;

import com.kgu.studywithme.auth.application.usecase.command.LogoutCommand;
import com.kgu.studywithme.auth.domain.service.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {
    private final TokenManager tokenManager;

    public void invoke(final LogoutCommand command) {
        tokenManager.deleteMemberRefreshToken(command.memberId());
    }
}
