package com.kgu.studywithme.auth.service;

import com.kgu.studywithme.auth.domain.Token;
import com.kgu.studywithme.auth.domain.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenManager {
    private final TokenRepository tokenRepository;

    @Transactional
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

    @Transactional
    public void reissueRefreshTokenByRtrPolicy(
            final Long memberId,
            final String newRefreshToken
    ) {
        tokenRepository.reissueRefreshTokenByRtrPolicy(memberId, newRefreshToken);
    }

    @Transactional
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
