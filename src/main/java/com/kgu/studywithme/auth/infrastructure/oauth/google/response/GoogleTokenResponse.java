package com.kgu.studywithme.auth.infrastructure.oauth.google.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthTokenResponse;

public class GoogleTokenResponse implements OAuthTokenResponse {
    private final String tokenType;
    private final String idToken;
    private final String accessToken;
    private final String scope;
    private final Integer expiresIn;

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

    public String getTokenType() {
        return tokenType;
    }

    public String getIdToken() {
        return idToken;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    public String getScope() {
        return scope;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }
}
