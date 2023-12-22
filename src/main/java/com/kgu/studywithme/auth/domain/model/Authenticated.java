package com.kgu.studywithme.auth.domain.model;

public record Authenticated(
        Long id,
        String accessToken
) {
}
