package com.kgu.studywithme.auth.infrastructure.token;

import com.kgu.studywithme.auth.domain.Token;
import com.kgu.studywithme.auth.domain.TokenRepository;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RdbTokenPersistenceAdapter implements TokenPersistenceAdapter {
    private final TokenRepository tokenRepository;

    @StudyWithMeWritableTransactional
    @Override
    public void synchronizeRefreshToken(Long memberId, String refreshToken) {
        tokenRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        token -> token.updateRefreshToken(refreshToken),
                        () -> tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken))
                );
    }

    @StudyWithMeWritableTransactional
    @Override
    public void reissueRefreshTokenByRtrPolicy(Long memberId, String refreshToken) {
        tokenRepository.reissueRefreshTokenByRtrPolicy(memberId, refreshToken);
    }

    @StudyWithMeWritableTransactional
    @Override
    public void deleteRefreshTokenByMemberId(Long memberId) {
        tokenRepository.deleteByMemberId(memberId);
    }

    @Override
    public boolean isRefreshTokenExists(Long memberId, String refreshToken) {
        return tokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken);
    }
}
