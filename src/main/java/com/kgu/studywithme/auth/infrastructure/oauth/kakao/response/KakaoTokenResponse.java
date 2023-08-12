package com.kgu.studywithme.auth.infrastructure.oauth.kakao.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthTokenResponse;

public record KakaoTokenResponse(
        String tokenType,
        String accessToken,
        String scope,
        Integer expiresIn
) implements OAuthTokenResponse {
    public KakaoTokenResponse(
            @JsonProperty("token_type") final String tokenType,
            @JsonProperty("access_token") final String accessToken,
            @JsonProperty("scope") final String scope,
            @JsonProperty("expires_in") final Integer expiresIn
    ) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.scope = scope;
        this.expiresIn = expiresIn;
    }
}
