package com.kgu.studywithme.common.mock.stub;

import com.kgu.studywithme.auth.application.adapter.OAuthUriGenerator;
import com.kgu.studywithme.auth.domain.model.oauth.OAuthProvider;

import static com.kgu.studywithme.auth.domain.model.oauth.OAuthProvider.GOOGLE;

public class StubOAuthUriGenerator implements OAuthUriGenerator {
    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return provider == GOOGLE;
    }

    @Override
    public String generate(final String redirectUri) {
        return "https://localhost:3000";
    }
}
