package com.kgu.studywithme.auth.infrastructure.oauth.google.response;

import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;

public record GoogleUserResponse(
        String name,
        String email,
        String picture
) implements OAuthUserResponse {
    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProfile() {
        return picture;
    }
}
