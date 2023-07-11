package com.kgu.studywithme.auth.infrastructure.oauth.google.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthTokenResponse;

public record GoogleTokenResponse(
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("id_token") String idToken,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("scope") String scope,
        @JsonProperty("expires_in") Integer expiresIn
) implements OAuthTokenResponse {
    @Override
    public String getAccessToken() {
        return accessToken;
    }
}
