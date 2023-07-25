package com.kgu.studywithme.auth.infrastructure.oauth;

import com.kgu.studywithme.auth.utils.OAuthProvider;

public interface OAuthConnector {
    boolean isSupported(final OAuthProvider provider);

    OAuthTokenResponse getToken(final String code, final String redirectUri);

    OAuthUserResponse getUserInfo(final String accessToken);
}
