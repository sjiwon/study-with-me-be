package com.kgu.studywithme.auth.application.usecase.query;

import com.kgu.studywithme.auth.domain.model.oauth.OAuthProvider;

public interface QueryOAuthLinkUseCase {
    String invoke(final Query query);

    record Query(
            OAuthProvider provider,
            String redirectUri
    ) {
    }
}
