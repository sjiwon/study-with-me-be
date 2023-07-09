package com.kgu.studywithme.auth.infrastructure.oauth.google;

import com.kgu.studywithme.auth.infrastructure.oauth.OAuthProperties;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUri;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleOAuthUri implements OAuthUri {
    private final OAuthProperties properties;

    @Override
    public String generate(final String redirectUri) {
        return properties.getAuthUrl() + "?"
                + "response_type=code&"
                + "client_id=" + properties.getClientId() + "&"
                + "scope=" + String.join(" ", properties.getScope()) + "&"
                + "redirect_uri=" + redirectUri;
    }
}
