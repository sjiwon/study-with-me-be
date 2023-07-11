package com.kgu.studywithme.auth.infrastructure.oauth;

public interface OAuthUserResponse {
    String getEmail();
    String getName();
    String getProfileImage();
}
