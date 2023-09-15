package com.kgu.studywithme.auth.application.usecase.command;

import com.kgu.studywithme.auth.domain.model.oauth.OAuthProvider;

public record OAuthLoginCommand(
        OAuthProvider provider,
        String code,
        String redirectUrl,
        String state
) {
}
