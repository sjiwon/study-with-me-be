package com.kgu.studywithme.auth.application.usecase;

import com.kgu.studywithme.auth.application.adapter.OAuthUriGenerator;
import com.kgu.studywithme.auth.application.usecase.query.GetOAuthLink;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetOAuthLinkUseCase {
    private final List<OAuthUriGenerator> oAuthUrisGenerators;

    public String invoke(final GetOAuthLink query) {
        final OAuthUriGenerator specificOAuthUriGenerator = oAuthUrisGenerators.stream()
                .filter(oAuthUri -> oAuthUri.isSupported(query.provider()))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(AuthErrorCode.INVALID_OAUTH_PROVIDER));

        return specificOAuthUriGenerator.generate(query.redirectUri());
    }
}
