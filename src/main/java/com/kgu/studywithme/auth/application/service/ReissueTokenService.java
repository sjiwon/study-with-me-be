package com.kgu.studywithme.auth.application.service;

import com.kgu.studywithme.auth.application.adapter.TokenPersistenceAdapter;
import com.kgu.studywithme.auth.application.usecase.command.ReissueTokenUseCase;
import com.kgu.studywithme.auth.domain.AuthToken;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueTokenService implements ReissueTokenUseCase {
    private final TokenPersistenceAdapter tokenPersistenceAdapter;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthToken invoke(final Command command) {
        if (isAnonymousRefreshToken(command.memberId(), command.refreshToken())) {
            throw StudyWithMeException.type(AuthErrorCode.INVALID_TOKEN);
        }

        return reissueMemberAuthToken(command.memberId());
    }

    private boolean isAnonymousRefreshToken(final Long memberId, final String refreshToken) {
        return !tokenPersistenceAdapter.isMemberRefreshToken(memberId, refreshToken);
    }

    private AuthToken reissueMemberAuthToken(final Long memberId) {
        final String newAccessToken = jwtTokenProvider.createAccessToken(memberId);
        final String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);
        tokenPersistenceAdapter.updateMemberRefreshToken(memberId, newRefreshToken);

        return new AuthToken(newAccessToken, newRefreshToken);
    }
}
