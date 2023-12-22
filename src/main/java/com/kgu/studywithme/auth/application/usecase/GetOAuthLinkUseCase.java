package com.kgu.studywithme.auth.application.usecase;

import com.kgu.studywithme.auth.application.adapter.OAuthUriGenerator;
import com.kgu.studywithme.auth.application.usecase.query.GetOAuthLink;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class GetOAuthLinkUseCase {
    private final List<OAuthUriGenerator> oAuthUrisGenerators;

    public String invoke(final GetOAuthLink query) {
        final OAuthUriGenerator oAuthUriGenerator = oAuthUrisGenerators.stream()
                .filter(oAuthUri -> oAuthUri.isSupported(query.provider()))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(AuthErrorCode.INVALID_OAUTH_PROVIDER));

        return oAuthUriGenerator.generate(query.redirectUri());
    }
}
