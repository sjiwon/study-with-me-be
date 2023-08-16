package com.kgu.studywithme.auth.application.service;

import com.kgu.studywithme.auth.application.adapter.OAuthConnector;
import com.kgu.studywithme.auth.application.adapter.TokenPersistenceAdapter;
import com.kgu.studywithme.auth.application.usecase.command.OAuthLoginUseCase;
import com.kgu.studywithme.auth.domain.AuthMember;
import com.kgu.studywithme.auth.domain.AuthToken;
import com.kgu.studywithme.auth.domain.oauth.OAuthTokenResponse;
import com.kgu.studywithme.auth.domain.oauth.OAuthUserResponse;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.auth.utils.OAuthProvider;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.global.exception.StudyWithMeOAuthException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OAuthLoginService implements OAuthLoginUseCase {
    private final List<OAuthConnector> oAuthConnectors;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenPersistenceAdapter tokenPersistenceAdapter;

    @Override
    public AuthMember invoke(final Command command) {
        final OAuthUserResponse oAuthUser = getOAuthUser(command);
        final Member member = getMemberByOAuthUser(oAuthUser);
        final AuthToken authToken = createAuthTokenAndPersist(member.getId());

        return new AuthMember(
                new AuthMember.MemberInfo(member),
                authToken
        );
    }

    private OAuthUserResponse getOAuthUser(final Command command) {
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

    private Member getMemberByOAuthUser(final OAuthUserResponse oAuthUser) {
        return memberRepository.findByEmail(oAuthUser.email())
                .orElseThrow(() -> new StudyWithMeOAuthException(oAuthUser));
    }

    private AuthToken createAuthTokenAndPersist(final Long memberId) {
        final String accessToken = jwtTokenProvider.createAccessToken(memberId);
        final String refreshToken = jwtTokenProvider.createRefreshToken(memberId);
        tokenPersistenceAdapter.synchronizeRefreshToken(memberId, refreshToken);

        return new AuthToken(accessToken, refreshToken);
    }
}
