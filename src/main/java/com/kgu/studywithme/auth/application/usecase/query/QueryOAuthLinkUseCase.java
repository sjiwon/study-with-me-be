package com.kgu.studywithme.auth.application.usecase.query;

import com.kgu.studywithme.auth.utils.OAuthProvider;

public interface QueryOAuthLinkUseCase {
    String createOAuthLink(Query query);

    record Query(
            OAuthProvider provider,
            String redirectUrl
    ) {
    }
}