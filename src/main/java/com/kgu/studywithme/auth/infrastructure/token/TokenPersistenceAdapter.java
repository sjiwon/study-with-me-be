package com.kgu.studywithme.auth.infrastructure.token;

public interface TokenPersistenceAdapter {
    void synchronizeRefreshToken(Long memberId, String refreshToken);

    void reissueRefreshTokenByRtrPolicy(Long memberId, String refreshToken);

    void deleteRefreshTokenByMemberId(Long memberId);

    boolean isRefreshTokenExists(Long memberId, String refreshToken);
}
