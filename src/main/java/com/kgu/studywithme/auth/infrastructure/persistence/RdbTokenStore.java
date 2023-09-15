package com.kgu.studywithme.auth.infrastructure.persistence;

import com.kgu.studywithme.auth.application.adapter.TokenStoreAdapter;
import com.kgu.studywithme.auth.domain.model.Token;
import com.kgu.studywithme.auth.domain.repository.TokenRepository;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RdbTokenStore implements TokenStoreAdapter {
    private final TokenRepository tokenRepository;

    @StudyWithMeWritableTransactional
    @Override
    public void synchronizeRefreshToken(final Long memberId, final String refreshToken) {
        tokenRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        token -> token.updateRefreshToken(refreshToken),
                        () -> tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken))
                );
    }

    @StudyWithMeWritableTransactional
    @Override
    public void updateRefreshToken(final Long memberId, final String refreshToken) {
        tokenRepository.updateRefreshToken(memberId, refreshToken);
    }

    @StudyWithMeWritableTransactional
    @Override
    public void deleteRefreshToken(final Long memberId) {
        tokenRepository.deleteRefreshToken(memberId);
    }

    @Override
    public boolean isMemberRefreshToken(final Long memberId, final String refreshToken) {
        return tokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken);
    }
}
