package com.kgu.studywithme.auth.infrastructure.oauth.google.response;

import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;

public record GoogleUserResponse(
        String name,
        String email,
        String picture
) implements OAuthUserResponse {
}
