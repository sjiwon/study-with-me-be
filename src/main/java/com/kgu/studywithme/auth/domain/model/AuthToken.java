package com.kgu.studywithme.auth.domain.model;

public record AuthToken(
        String accessToken,
        String refreshToken
) {
}
