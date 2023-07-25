package com.kgu.studywithme.auth.infrastructure.token;

public interface TokenPersistenceAdapter {
    void synchronizeRefreshToken(final Long memberId, final String refreshToken);

    void reissueRefreshTokenByRtrPolicy(final Long memberId, final String refreshToken);

    void deleteRefreshTokenByMemberId(final Long memberId);

    boolean isRefreshTokenExists(final Long memberId, final String refreshToken);
}
