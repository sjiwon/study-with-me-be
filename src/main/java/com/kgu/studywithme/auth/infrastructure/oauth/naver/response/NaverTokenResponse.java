package com.kgu.studywithme.auth.infrastructure.oauth.naver.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthTokenResponse;

public record NaverTokenResponse(
        String tokenType,
        String accessToken,
        Integer expiresIn
) implements OAuthTokenResponse {
    public NaverTokenResponse(
            @JsonProperty("token_type") final String tokenType,
            @JsonProperty("access_token") final String accessToken,
            @JsonProperty("expires_in") final Integer expiresIn
    ) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}
