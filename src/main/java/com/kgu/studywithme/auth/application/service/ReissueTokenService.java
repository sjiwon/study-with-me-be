package com.kgu.studywithme.auth.application.service;

import com.kgu.studywithme.auth.application.dto.TokenResponse;
import com.kgu.studywithme.auth.application.usecase.command.ReissueTokenUseCase;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infrastructure.token.TokenPersistenceAdapter;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class ReissueTokenService implements ReissueTokenUseCase {
    private final TokenPersistenceAdapter tokenPersistenceAdapter;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public TokenResponse reissueToken(final Command command) {
        if (!tokenPersistenceAdapter.isRefreshTokenExists(command.memberId(), command.refreshToken())) {
            throw StudyWithMeException.type(AuthErrorCode.INVALID_TOKEN);
        }

        final String newAccessToken = jwtTokenProvider.createAccessToken(command.memberId());
        final String newRefreshToken = jwtTokenProvider.createRefreshToken(command.memberId());
        tokenPersistenceAdapter.reissueRefreshTokenByRtrPolicy(command.memberId(), newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
