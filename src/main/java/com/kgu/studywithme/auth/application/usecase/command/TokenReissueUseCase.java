package com.kgu.studywithme.auth.application.usecase.command;

import com.kgu.studywithme.auth.application.dto.response.TokenResponse;

public interface TokenReissueUseCase {
    TokenResponse reissueTokens(final Command command);

    record Command(
            Long memberId,
            String refreshToken
    ) {
    }
}
