package com.kgu.studywithme.auth.infrastructure.oauth.kakao;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Getter
@Component
public class KakaoOAuthProperties {
    private final String grantType;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final Set<String> scope;
    private final String authUrl;
    private final String tokenUrl;
    private final String userInfoUrl;

    public KakaoOAuthProperties(
            @Value("${oauth2.kakao.grant-type}") final String grantType,
            @Value("${oauth2.kakao.client-id}") final String clientId,
            @Value("${oauth2.kakao.client-secret}") final String clientSecret,
            @Value("${oauth2.kakao.redirect-uri}") final String redirectUri,
            @Value("${oauth2.kakao.scope}") final Set<String> scope,
            @Value("${oauth2.kakao.auth-url}") final String authUrl,
            @Value("${oauth2.kakao.token-url}") final String tokenUrl,
            @Value("${oauth2.kakao.user-info-url}") final String userInfoUrl
    ) {
        this.grantType = grantType;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.scope = scope;
        this.authUrl = authUrl;
        this.tokenUrl = tokenUrl;
        this.userInfoUrl = userInfoUrl;
    }
}
