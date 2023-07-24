package com.kgu.studywithme.auth.application.usecase.command;

import com.kgu.studywithme.auth.application.dto.TokenResponse;

public interface ReissueTokenUseCase {
    TokenResponse reissueToken(final Command command);

    record Command(
            Long memberId,
            String refreshToken
    ) {
    }
}
