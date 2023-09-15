package com.kgu.studywithme.auth.application.usecase.query;

import com.kgu.studywithme.auth.domain.model.oauth.OAuthProvider;

public record OAuthLinkQuery(
        OAuthProvider provider,
        String redirectUri
) {
}
