package com.kgu.studywithme.global;

public record Authenticated(
        Long id,
        String accessToken
) {
}
