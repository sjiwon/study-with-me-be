package com.kgu.studywithme.auth.application.usecase.command;

import com.kgu.studywithme.auth.application.dto.TokenResponse;

public interface TokenReissueUseCase {
    TokenResponse reissueTokens(final Command command);

    record Command(
            Long memberId,
            String refreshToken
    ) {
    }
}
