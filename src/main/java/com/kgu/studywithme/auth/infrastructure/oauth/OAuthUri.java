package com.kgu.studywithme.auth.infrastructure.oauth;

import com.kgu.studywithme.auth.utils.OAuthProvider;

public interface OAuthUri {
    boolean isSupported(final OAuthProvider provider);

    String generate(final String redirectUri);
}
