package com.kgu.studywithme.auth.infrastructure.oauth;

public interface OAuthUserResponse {
    String email();

    String name();

    String profileImage();
}
