package com.kgu.studywithme.auth.application.usecase;

import com.kgu.studywithme.auth.application.usecase.command.LogoutCommand;
import com.kgu.studywithme.auth.domain.service.TokenIssuer;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {
    private final TokenIssuer tokenIssuer;

    @StudyWithMeWritableTransactional
    public void invoke(final LogoutCommand command) {
        tokenIssuer.deleteRefreshToken(command.memberId());
    }
}
