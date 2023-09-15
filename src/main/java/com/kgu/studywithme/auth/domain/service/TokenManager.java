package com.kgu.studywithme.auth.domain.service;

import com.kgu.studywithme.auth.application.adapter.TokenPersistenceAdapter;
import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.utils.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenManager {
    private final TokenProvider tokenProvider;
    private final TokenPersistenceAdapter tokenPersistenceAdapter;

    public AuthToken provideAuthorityToken(final Long memberId) {
        final String accessToken = tokenProvider.createAccessToken(memberId);
        final String refreshToken = tokenProvider.createRefreshToken(memberId);
        tokenPersistenceAdapter.synchronizeRefreshToken(memberId, refreshToken);

        return new AuthToken(accessToken, refreshToken);
    }

    public AuthToken reissueAuthorityToken(final Long memberId) {
        final String newAccessToken = tokenProvider.createAccessToken(memberId);
        final String newRefreshToken = tokenProvider.createRefreshToken(memberId);
        tokenPersistenceAdapter.updateMemberRefreshToken(memberId, newRefreshToken);

        return new AuthToken(newAccessToken, newRefreshToken);
    }

    public boolean isMemberRefreshToken(final Long memberId, final String refreshToken) {
        return tokenPersistenceAdapter.isMemberRefreshToken(memberId, refreshToken);
    }

    public void deleteMemberRefreshToken(final Long memberId) {
        tokenPersistenceAdapter.deleteMemberRefreshToken(memberId);
    }
}
