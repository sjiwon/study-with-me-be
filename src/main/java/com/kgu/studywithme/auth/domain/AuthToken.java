package com.kgu.studywithme.auth.domain;

public record AuthToken(
        String accessToken,
        String refreshToken
) {
}
