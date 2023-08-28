package com.kgu.studywithme.common.stub;

import com.kgu.studywithme.auth.application.adapter.OAuthConnector;
import com.kgu.studywithme.auth.domain.oauth.OAuthProvider;
import com.kgu.studywithme.auth.domain.oauth.OAuthTokenResponse;
import com.kgu.studywithme.auth.domain.oauth.OAuthUserResponse;
import com.kgu.studywithme.common.fixture.OAuthFixture;

import static com.kgu.studywithme.auth.domain.oauth.OAuthProvider.GOOGLE;

public class StubOAuthConnector implements OAuthConnector {
    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return provider == GOOGLE;
    }

    @Override
    public OAuthTokenResponse fetchToken(final String code, final String redirectUri, final String state) {
        return OAuthFixture.parseOAuthTokenByCode(code);
    }

    @Override
    public OAuthUserResponse fetchUserInfo(final String accessToken) {
        return OAuthFixture.parseOAuthUserByAccessToken(accessToken);
    }
}
