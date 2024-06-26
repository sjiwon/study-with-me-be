package com.kgu.studywithme.auth.utils;

public interface TokenProvider {
    String createAccessToken(final Long memberId);

    String createRefreshToken(final Long memberId);

    Long getId(final String token);

    void validateToken(final String token);
}
