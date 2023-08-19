package com.kgu.studywithme.auth.application.adapter;

import com.kgu.studywithme.auth.domain.oauth.OAuthTokenResponse;
import com.kgu.studywithme.auth.domain.oauth.OAuthUserResponse;
import com.kgu.studywithme.auth.utils.OAuthProvider;

public interface OAuthConnector {
    boolean isSupported(final OAuthProvider provider);

    OAuthTokenResponse fetchToken(final String code, final String redirectUri, final String state);

    OAuthUserResponse fetchUserInfo(final String accessToken);
}
