package com.kgu.studywithme.auth.infrastructure.oauth.naver;

import com.kgu.studywithme.auth.application.adapter.OAuthUriGenerator;
import com.kgu.studywithme.auth.domain.oauth.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static com.kgu.studywithme.auth.domain.oauth.OAuthProvider.NAVER;

@Component
@RequiredArgsConstructor
public class NaverOAuthUriGenerator implements OAuthUriGenerator {
    private final NaverOAuthProperties properties;

    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return provider == NAVER;
    }

    @Override
    public String generate(final String redirectUri) {
        return UriComponentsBuilder
                .fromUriString(properties.getAuthUrl())
                .queryParam("response_type", "code")
                .queryParam("client_id", properties.getClientId())
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", UUID.randomUUID().toString().replaceAll("-", ""))
                .build()
                .toUriString();
    }
}
