package com.kgu.studywithme.auth.infrastructure.oauth.google.response;

import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoogleUserResponse implements OAuthUserResponse {
    private final String name;
    private final String email;
    private final String picture;

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProfileImage() {
        return picture;
    }
}
