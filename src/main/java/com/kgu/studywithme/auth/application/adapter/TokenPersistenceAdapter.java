package com.kgu.studywithme.auth.application.adapter;

public interface TokenPersistenceAdapter {
    void synchronizeRefreshToken(final Long memberId, final String refreshToken);

    void updateMemberRefreshToken(final Long memberId, final String refreshToken);

    void deleteMemberRefreshToken(final Long memberId);

    boolean isMemberRefreshToken(final Long memberId, final String refreshToken);
}
