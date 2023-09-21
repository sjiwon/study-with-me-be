package com.kgu.studywithme.auth.application.adapter;

public interface TokenStoreAdapter {
    void synchronizeRefreshToken(final Long memberId, final String refreshToken);

    void updateRefreshToken(final Long memberId, final String refreshToken);

    void deleteRefreshToken(final Long memberId);

    boolean isMemberRefreshToken(final Long memberId, final String refreshToken);
}
