package com.kgu.studywithme.auth.infrastructure.oauth.google.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kgu.studywithme.auth.domain.oauth.OAuthTokenResponse;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleTokenResponse(
        String tokenType,
        String idToken,
        String accessToken,
        String refreshToken,
        long expiresIn
) implements OAuthTokenResponse {
}
