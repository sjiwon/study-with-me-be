package com.kgu.studywithme.auth.infrastructure.oauth.google;

import com.kgu.studywithme.auth.application.adapter.OAuthUriGenerator;
import com.kgu.studywithme.auth.domain.model.oauth.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static com.kgu.studywithme.auth.domain.model.oauth.OAuthProvider.GOOGLE;

@Component
@RequiredArgsConstructor
public class GoogleOAuthUriGenerator implements OAuthUriGenerator {
    private final GoogleOAuthProperties properties;

    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return provider == GOOGLE;
    }

    @Override
    public String generate(final String redirectUri) {
        return UriComponentsBuilder
                .fromUriString(properties.getAuthUrl())
                .queryParam("response_type", "code")
                .queryParam("client_id", properties.getClientId())
                .queryParam("scope", String.join(" ", properties.getScope()))
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", UUID.randomUUID().toString().replaceAll("-", ""))
                .build()
                .toUriString();
    }
}
