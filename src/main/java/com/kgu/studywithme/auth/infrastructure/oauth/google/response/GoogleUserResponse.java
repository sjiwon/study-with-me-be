package com.kgu.studywithme.auth.infrastructure.oauth.google.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;

public record GoogleUserResponse(
        String name,
        String email,
        String profileImage
) implements OAuthUserResponse {
    public GoogleUserResponse(
            @JsonProperty("name") final String name,
            @JsonProperty("email") final String email,
            @JsonProperty("picture") final String profileImage
    ) {
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
    }
}
