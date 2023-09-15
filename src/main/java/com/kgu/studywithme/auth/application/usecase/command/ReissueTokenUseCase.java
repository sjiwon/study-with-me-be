package com.kgu.studywithme.auth.application.usecase.command;

import com.kgu.studywithme.auth.domain.model.AuthToken;

public interface ReissueTokenUseCase {
    AuthToken invoke(final Command command);

    record Command(
            Long memberId,
            String refreshToken
    ) {
    }
}
