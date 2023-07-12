package com.kgu.studywithme.auth.application;

import com.kgu.studywithme.auth.application.dto.response.TokenResponse;
import com.kgu.studywithme.auth.application.usecase.command.TokenReissueUseCase;
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
public class TokenReissueService implements TokenReissueUseCase {
    private final TokenPersistenceAdapter tokenPersistenceAdapter;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public TokenResponse reissueTokens(final Command command) {
        // 사용자가 보유하고 있는 Refresh Token인지
        if (!tokenPersistenceAdapter.isRefreshTokenExists(command.memberId(), command.refreshToken())) {
            throw StudyWithMeException.type(AuthErrorCode.AUTH_INVALID_TOKEN);
        }

        // Access Token & Refresh Token 발급
        final String newAccessToken = jwtTokenProvider.createAccessToken(command.memberId());
        final String newRefreshToken = jwtTokenProvider.createRefreshToken(command.memberId());

        // RTR 정책에 의해 memberId에 해당하는 사용자가 보유하고 있는 Refresh Token 업데이트
        tokenPersistenceAdapter.reissueRefreshTokenByRtrPolicy(command.memberId(), newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
