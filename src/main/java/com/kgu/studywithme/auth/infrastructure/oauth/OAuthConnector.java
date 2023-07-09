package com.kgu.studywithme.auth.infrastructure.oauth;

public interface OAuthConnector {
    OAuthTokenResponse getToken(String code, String redirectUri);

    OAuthUserResponse getUserInfo(String accessToken);
}
