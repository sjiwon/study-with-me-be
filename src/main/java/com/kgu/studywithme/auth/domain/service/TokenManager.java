package com.kgu.studywithme.auth.domain.service;

import com.kgu.studywithme.auth.domain.model.Token;
import com.kgu.studywithme.auth.domain.repository.TokenRepository;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenManager {
    private final TokenRepository tokenRepository;

    @StudyWithMeWritableTransactional
    public void synchronizeRefreshToken(final Long memberId, final String refreshToken) {
        tokenRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        token -> token.updateRefreshToken(refreshToken),
                        () -> tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken))
                );
    }

    public void updateRefreshToken(final Long memberId, final String newRefreshToken) {
        tokenRepository.updateRefreshToken(memberId, newRefreshToken);
    }

    public void deleteRefreshToken(final Long memberId) {
        tokenRepository.deleteRefreshToken(memberId);
    }

    public boolean isMemberRefreshToken(final Long memberId, final String refreshToken) {
        return tokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken);
    }
}
