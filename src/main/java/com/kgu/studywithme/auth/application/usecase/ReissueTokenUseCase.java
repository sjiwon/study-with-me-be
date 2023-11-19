package com.kgu.studywithme.auth.application.usecase;

import com.kgu.studywithme.auth.application.usecase.command.ReissueTokenCommand;
import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.domain.service.TokenIssuer;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueTokenUseCase {
    private final TokenIssuer tokenIssuer;

    @StudyWithMeWritableTransactional
    public AuthToken invoke(final ReissueTokenCommand command) {
        if (isAnonymousRefreshToken(command.memberId(), command.refreshToken())) {
            throw StudyWithMeException.type(AuthErrorCode.INVALID_TOKEN);
        }

        return tokenIssuer.reissueAuthorityToken(command.memberId());
    }

    private boolean isAnonymousRefreshToken(final Long memberId, final String refreshToken) {
        return !tokenIssuer.isMemberRefreshToken(memberId, refreshToken);
    }
}
