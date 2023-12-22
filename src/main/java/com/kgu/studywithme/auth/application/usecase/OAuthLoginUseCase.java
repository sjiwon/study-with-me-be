package com.kgu.studywithme.auth.application.usecase;

import com.kgu.studywithme.auth.application.adapter.OAuthConnector;
import com.kgu.studywithme.auth.application.usecase.command.OAuthLoginCommand;
import com.kgu.studywithme.auth.domain.model.AuthMember;
import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.domain.model.oauth.OAuthProvider;
import com.kgu.studywithme.auth.domain.model.oauth.OAuthTokenResponse;
import com.kgu.studywithme.auth.domain.model.oauth.OAuthUserResponse;
import com.kgu.studywithme.auth.domain.service.TokenIssuer;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.global.exception.StudyWithMeOAuthException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class OAuthLoginUseCase {
    private final List<OAuthConnector> oAuthConnectors;
    private final MemberRepository memberRepository;
    private final TokenIssuer tokenIssuer;

    public AuthMember invoke(final OAuthLoginCommand command) {
        final OAuthUserResponse oAuthUser = getOAuthUser(command);
        final Member member = getMemberByOAuthEmail(oAuthUser);
        final AuthToken authToken = tokenIssuer.provideAuthorityToken(member.getId());

        return new AuthMember(
                new AuthMember.MemberInfo(member),
                authToken
        );
    }

    private OAuthUserResponse getOAuthUser(final OAuthLoginCommand command) {
        final OAuthConnector oAuthConnector = getOAuthConnectorByProvider(command.provider());
        final OAuthTokenResponse oAuthToken = oAuthConnector.fetchToken(command.code(), command.redirectUrl(), command.state());

        return oAuthConnector.fetchUserInfo(oAuthToken.accessToken());
    }

    private OAuthConnector getOAuthConnectorByProvider(final OAuthProvider provider) {
        return oAuthConnectors.stream()
                .filter(oAuthConnector -> oAuthConnector.isSupported(provider))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(AuthErrorCode.INVALID_OAUTH_PROVIDER));
    }

    private Member getMemberByOAuthEmail(final OAuthUserResponse oAuthUser) {
        return memberRepository.findByEmail(oAuthUser.email())
                .orElseThrow(() -> new StudyWithMeOAuthException(oAuthUser));
    }
}
