package com.kgu.studywithme.global.exception.dto;

import com.kgu.studywithme.auth.domain.oauth.OAuthUserResponse;

public record OAuthExceptionResponse(
        String name,
        String email
) {
    public OAuthExceptionResponse(final OAuthUserResponse oAuthUserResponse) {
        this(
                oAuthUserResponse.name(),
                oAuthUserResponse.email()
        );
    }
}