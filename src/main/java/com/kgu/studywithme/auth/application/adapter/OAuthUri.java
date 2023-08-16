package com.kgu.studywithme.auth.application.adapter;

import com.kgu.studywithme.auth.utils.OAuthProvider;

public interface OAuthUri {
    boolean isSupported(final OAuthProvider provider);

    String generate(final String redirectUri);
}
