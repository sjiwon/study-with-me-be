package com.kgu.studywithme.common.mock.fake;

import com.kgu.studywithme.auth.application.adapter.TokenPersistenceAdapter;

import java.util.HashMap;
import java.util.Map;

public class FakeTokenPersistenceAdapter implements TokenPersistenceAdapter {
    private final Map<Long, String> tokenStore = new HashMap<>();

    @Override
    public void synchronizeRefreshToken(final Long memberId, final String refreshToken) {
        tokenStore.put(memberId, refreshToken);
    }

    @Override
    public void updateMemberRefreshToken(final Long memberId, final String refreshToken) {
        tokenStore.put(memberId, refreshToken);
    }

    @Override
    public void deleteMemberRefreshToken(final Long memberId) {
        tokenStore.remove(memberId);
    }

    @Override
    public boolean isMemberRefreshToken(final Long memberId, final String refreshToken) {
        return refreshToken.equals(tokenStore.get(memberId));
    }
}
