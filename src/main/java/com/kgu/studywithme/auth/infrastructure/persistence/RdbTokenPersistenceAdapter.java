package com.kgu.studywithme.auth.infrastructure.persistence;

import com.kgu.studywithme.auth.application.adapter.TokenPersistenceAdapter;
import com.kgu.studywithme.auth.domain.Token;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RdbTokenPersistenceAdapter implements TokenPersistenceAdapter {
    private final TokenJpaRepository tokenJpaRepository;

    @StudyWithMeWritableTransactional
    @Override
    public void synchronizeRefreshToken(
            final Long memberId,
            final String refreshToken
    ) {
        tokenJpaRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        token -> token.updateRefreshToken(refreshToken),
                        () -> tokenJpaRepository.save(Token.issueRefreshToken(memberId, refreshToken))
                );
    }

    @StudyWithMeWritableTransactional
    @Override
    public void updateMemberRefreshToken(
            final Long memberId,
            final String refreshToken
    ) {
        tokenJpaRepository.updateMemberRefreshToken(memberId, refreshToken);
    }

    @StudyWithMeWritableTransactional
    @Override
    public void deleteMemberRefreshToken(final Long memberId) {
        tokenJpaRepository.deleteMemberRefreshToken(memberId);
    }

    @Override
    public boolean isMemberRefreshToken(
            final Long memberId,
            final String refreshToken
    ) {
        return tokenJpaRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken);
    }
}
