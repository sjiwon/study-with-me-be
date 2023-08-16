package com.kgu.studywithme.auth.application.usecase.command;

import com.kgu.studywithme.auth.domain.AuthMember;
import com.kgu.studywithme.auth.utils.OAuthProvider;

public interface OAuthLoginUseCase {
    AuthMember invoke(final Command command);

    record Command(
            OAuthProvider provider,
            String code,
            String redirectUrl,
            String state
    ) {
    }
}
