package com.kgu.studywithme.common.mock.fake;

import com.kgu.studywithme.auth.application.adapter.TokenStoreAdapter;

import java.util.HashMap;
import java.util.Map;

public class FakeTokenStore implements TokenStoreAdapter {
    private final Map<Long, String> tokenStore = new HashMap<>();

    @Override
    public void synchronizeRefreshToken(final Long memberId, final String refreshToken) {
        tokenStore.put(memberId, refreshToken);
    }

    @Override
    public void updateRefreshToken(final Long memberId, final String refreshToken) {
        tokenStore.put(memberId, refreshToken);
    }

    @Override
    public void deleteRefreshToken(final Long memberId) {
        tokenStore.remove(memberId);
    }

    @Override
    public boolean isMemberRefreshToken(final Long memberId, final String refreshToken) {
        return refreshToken.equals(tokenStore.get(memberId));
    }
}
