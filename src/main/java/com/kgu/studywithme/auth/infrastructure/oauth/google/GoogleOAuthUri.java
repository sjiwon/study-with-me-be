package com.kgu.studywithme.auth.infrastructure.oauth.google;

import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUri;
import com.kgu.studywithme.auth.utils.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.kgu.studywithme.auth.utils.OAuthProvider.GOOGLE;

@Component
@RequiredArgsConstructor
public class GoogleOAuthUri implements OAuthUri {
    private final GoogleOAuthProperties properties;

    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return provider == GOOGLE;
    }

    @Override
    public String generate(final String redirectUri) {
        return properties.getAuthUrl() + "?"
                + "response_type=code&"
                + "client_id=" + properties.getClientId() + "&"
                + "scope=" + String.join(" ", properties.getScope()) + "&"
                + "redirect_uri=" + redirectUri + "&"
                + "state=" + UUID.randomUUID().toString().replaceAll("-", "");
    }
}
