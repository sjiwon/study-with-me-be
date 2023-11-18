package com.kgu.studywithme.auth.domain.service;

import com.kgu.studywithme.auth.application.adapter.TokenStoreAdapter;
import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.utils.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenIssuer {
    private final TokenProvider tokenProvider;
    private final TokenStoreAdapter tokenStoreAdapter;

    public AuthToken provideAuthorityToken(final Long memberId) {
        final String accessToken = tokenProvider.createAccessToken(memberId);
        final String refreshToken = tokenProvider.createRefreshToken(memberId);
        tokenStoreAdapter.synchronizeRefreshToken(memberId, refreshToken);

        return new AuthToken(accessToken, refreshToken);
    }

    public AuthToken reissueAuthorityToken(final Long memberId) {
        final String newAccessToken = tokenProvider.createAccessToken(memberId);
        final String newRefreshToken = tokenProvider.createRefreshToken(memberId);
        tokenStoreAdapter.updateRefreshToken(memberId, newRefreshToken);

        return new AuthToken(newAccessToken, newRefreshToken);
    }

    public boolean isMemberRefreshToken(final Long memberId, final String refreshToken) {
        return tokenStoreAdapter.isMemberRefreshToken(memberId, refreshToken);
    }

    public void deleteRefreshToken(final Long memberId) {
        tokenStoreAdapter.deleteRefreshToken(memberId);
    }
}
