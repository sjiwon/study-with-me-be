package com.kgu.studywithme.common.stub;

import com.kgu.studywithme.auth.infrastructure.oauth.OAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;
import com.kgu.studywithme.auth.utils.OAuthProvider;
import com.kgu.studywithme.common.fixture.OAuthFixture;

import static com.kgu.studywithme.auth.utils.OAuthProvider.GOOGLE;

public class StubOAuthConnector implements OAuthConnector {
    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return provider == GOOGLE;
    }

    @Override
    public OAuthTokenResponse getToken(final String code, final String redirectUri) {
        return OAuthFixture.parseOAuthTokenByCode(code);
    }

    @Override
    public OAuthUserResponse getUserInfo(final String accessToken) {
        return OAuthFixture.parseOAuthUserByAccessToken(accessToken);
    }
}
