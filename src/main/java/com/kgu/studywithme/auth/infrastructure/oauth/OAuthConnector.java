package com.kgu.studywithme.auth.infrastructure.oauth;

import com.kgu.studywithme.auth.utils.OAuthProvider;

public interface OAuthConnector {
    boolean isSupported(OAuthProvider provider);

    OAuthTokenResponse getToken(String code, String redirectUri);

    OAuthUserResponse getUserInfo(String accessToken);
}
