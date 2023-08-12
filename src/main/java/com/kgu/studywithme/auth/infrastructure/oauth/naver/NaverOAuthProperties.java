package com.kgu.studywithme.auth.infrastructure.oauth.naver;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class NaverOAuthProperties {
    private final String grantType;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String authUrl;
    private final String tokenUrl;
    private final String userInfoUrl;

    public NaverOAuthProperties(
            @Value("${oauth2.naver.grant-type}") final String grantType,
            @Value("${oauth2.naver.client-id}") final String clientId,
            @Value("${oauth2.naver.client-secret}") final String clientSecret,
            @Value("${oauth2.naver.redirect-uri}") final String redirectUri,
            @Value("${oauth2.naver.auth-url}") final String authUrl,
            @Value("${oauth2.naver.token-url}") final String tokenUrl,
            @Value("${oauth2.naver.user-info-url}") final String userInfoUrl
    ) {
        this.grantType = grantType;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.authUrl = authUrl;
        this.tokenUrl = tokenUrl;
        this.userInfoUrl = userInfoUrl;
    }
}
