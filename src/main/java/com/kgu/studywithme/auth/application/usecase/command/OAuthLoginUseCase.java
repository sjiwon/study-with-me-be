package com.kgu.studywithme.auth.application.usecase.command;

import com.kgu.studywithme.auth.application.dto.response.LoginResponse;
import com.kgu.studywithme.auth.utils.OAuthProvider;

public interface OAuthLoginUseCase {
    LoginResponse login(Command command);

    record Command(
            OAuthProvider provider,
            String code,
            String redirectUrl
    ) {
    }
}
