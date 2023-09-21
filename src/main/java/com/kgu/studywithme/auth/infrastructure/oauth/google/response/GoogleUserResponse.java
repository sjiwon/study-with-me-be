package com.kgu.studywithme.auth.infrastructure.oauth.google.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kgu.studywithme.auth.domain.model.oauth.OAuthUserResponse;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleUserResponse(
        String sub,
        String name,
        String givenName,
        String familyName,
        String picture,
        String email,
        boolean emailVerified,
        String locale
) implements OAuthUserResponse {
}
