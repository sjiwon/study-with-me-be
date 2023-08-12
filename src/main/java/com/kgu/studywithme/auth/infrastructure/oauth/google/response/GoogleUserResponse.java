package com.kgu.studywithme.auth.infrastructure.oauth.google.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;

public record GoogleUserResponse(
        // required
        String name,
        String email,

        // other
        String sub,
        String givenName,
        String familyName,
        String picture,
        boolean emailVerified,
        String locale
) implements OAuthUserResponse {
    public GoogleUserResponse(
            @JsonProperty("name") final String name,
            @JsonProperty("email") final String email,
            @JsonProperty("sub") final String sub,
            @JsonProperty("given_name") final String givenName,
            @JsonProperty("family_name") final String familyName,
            @JsonProperty("picture") final String picture,
            @JsonProperty("email_verified") final boolean emailVerified,
            @JsonProperty("locale") final String locale
    ) {
        this.name = name;
        this.email = email;
        this.sub = sub;
        this.givenName = givenName;
        this.familyName = familyName;
        this.picture = picture;
        this.emailVerified = emailVerified;
        this.locale = locale;
    }
}
