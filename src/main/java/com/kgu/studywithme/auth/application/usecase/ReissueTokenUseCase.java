package com.kgu.studywithme.auth.application.usecase;

import com.kgu.studywithme.auth.application.usecase.command.ReissueTokenCommand;
import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.domain.service.TokenIssuer;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.utils.TokenProvider;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueTokenUseCase {
    private final TokenProvider tokenProvider;
    private final TokenIssuer tokenIssuer;

    @StudyWithMeWritableTransactional
    public AuthToken invoke(final ReissueTokenCommand command) {
        final Long memberId = tokenProvider.getId(command.refreshToken());
        validateMemberToken(memberId, command.refreshToken());
        return tokenIssuer.reissueAuthorityToken(memberId);
    }

    private void validateMemberToken(final Long memberId, final String refreshToken) {
        if (isAnonymousRefreshToken(memberId, refreshToken)) {
            throw StudyWithMeException.type(AuthErrorCode.INVALID_TOKEN);
        }
    }

    private boolean isAnonymousRefreshToken(final Long memberId, final String refreshToken) {
        return !tokenIssuer.isMemberRefreshToken(memberId, refreshToken);
    }
}
