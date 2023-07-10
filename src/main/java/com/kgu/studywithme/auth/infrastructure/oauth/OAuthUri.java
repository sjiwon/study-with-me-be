package com.kgu.studywithme.auth.infrastructure.oauth;

import com.kgu.studywithme.auth.utils.OAuthProvider;

public interface OAuthUri {
    boolean isSupported(OAuthProvider provider);

    String generate(String redirectUri);
}
