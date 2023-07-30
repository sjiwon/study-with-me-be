package com.kgu.studywithme.common.stub;

import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUri;
import com.kgu.studywithme.auth.utils.OAuthProvider;

public class StubOAuthUri implements OAuthUri {
    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return true;
    }

    @Override
    public String generate(final String redirectUri) {
        return "https://localhost:3000";
    }
}
