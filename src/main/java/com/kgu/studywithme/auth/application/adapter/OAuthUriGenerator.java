package com.kgu.studywithme.auth.application.adapter;

import com.kgu.studywithme.auth.domain.oauth.OAuthProvider;

public interface OAuthUriGenerator {
    boolean isSupported(final OAuthProvider provider);

    String generate(final String redirectUri);
}
