package com.kgu.studywithme.auth.infrastructure.oauth;

@FunctionalInterface
public interface OAuthUri {
    String generate(String redirectUri);
}
