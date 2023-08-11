package com.kgu.studywithme.auth.infrastructure.oauth.google.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthTokenResponse;

public record GoogleTokenResponse(
        String tokenType,
        String idToken,
        String accessToken,
        String scope,
        Integer expiresIn
) implements OAuthTokenResponse {
    public GoogleTokenResponse(
            @JsonProperty("token_type") final String tokenType,
            @JsonProperty("id_token") final String idToken,
            @JsonProperty("access_token") final String accessToken,
            @JsonProperty("scope") final String scope,
            @JsonProperty("expires_in") final Integer expiresIn
    ) {
        this.tokenType = tokenType;
        this.idToken = idToken;
        this.accessToken = accessToken;
        this.scope = scope;
        this.expiresIn = expiresIn;
    }
}
