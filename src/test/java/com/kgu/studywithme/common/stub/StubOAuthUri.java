package com.kgu.studywithme.common.stub;

import com.kgu.studywithme.auth.application.adapter.OAuthUri;
import com.kgu.studywithme.auth.utils.OAuthProvider;

import static com.kgu.studywithme.auth.utils.OAuthProvider.GOOGLE;

public class StubOAuthUri implements OAuthUri {
    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return provider == GOOGLE;
    }

    @Override
    public String generate(final String redirectUri) {
        return "https://localhost:3000";
    }
}
