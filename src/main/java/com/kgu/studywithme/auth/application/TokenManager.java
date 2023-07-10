package com.kgu.studywithme.auth.application;

import com.kgu.studywithme.auth.domain.Token;
import com.kgu.studywithme.auth.domain.TokenRepository;
import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class TokenManager {
    private final TokenRepository tokenRepository;

    @StudyWithMeWritableTransactional
    public void synchronizeRefreshToken(
            final Long memberId,
            final String refreshToken
    ) {
        tokenRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        token -> token.updateRefreshToken(refreshToken),
                        () -> tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken))
                );
    }

    @StudyWithMeWritableTransactional
    public void reissueRefreshTokenByRtrPolicy(
            final Long memberId,
            final String newRefreshToken
    ) {
        tokenRepository.reissueRefreshTokenByRtrPolicy(memberId, newRefreshToken);
    }

    @StudyWithMeWritableTransactional
    public void deleteRefreshTokenByMemberId(final Long memberId) {
        tokenRepository.deleteByMemberId(memberId);
    }

    public boolean isRefreshTokenExists(
            final Long memberId,
            final String refreshToken
    ) {
        return tokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken);
    }
}
