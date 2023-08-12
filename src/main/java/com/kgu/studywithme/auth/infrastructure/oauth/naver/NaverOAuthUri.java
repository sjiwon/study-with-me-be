package com.kgu.studywithme.auth.infrastructure.oauth.naver;

import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUri;
import com.kgu.studywithme.auth.utils.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.kgu.studywithme.auth.utils.OAuthProvider.NAVER;

@Component
@RequiredArgsConstructor
public class NaverOAuthUri implements OAuthUri {
    private final NaverOAuthProperties properties;

    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return provider == NAVER;
    }

    @Override
    public String generate(final String redirectUri) {
        return properties.getAuthUrl() + "?"
                + "response_type=code&"
                + "client_id=" + properties.getClientId() + "&"
                + "redirect_uri=" + redirectUri + "&"
                + "state=" + UUID.randomUUID().toString().replaceAll("-", "");
    }
}
