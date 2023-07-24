package com.kgu.studywithme.auth.application.service;

import com.kgu.studywithme.auth.application.usecase.query.QueryOAuthLinkUseCase;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUri;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryOAuthLinkService implements QueryOAuthLinkUseCase {
    private final List<OAuthUri> oAuthUris;

    @Override
    public String queryOAuthLink(final Query query) {
        final OAuthUri findSpecificOAuthUri = oAuthUris.stream()
                .filter(oAuthUri -> oAuthUri.isSupported(query.provider()))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(AuthErrorCode.INVALID_OAUTH_PROVIDER));

        return findSpecificOAuthUri.generate(query.redirectUrl());
    }
}
