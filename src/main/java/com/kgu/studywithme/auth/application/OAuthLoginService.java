package com.kgu.studywithme.auth.application;

import com.kgu.studywithme.auth.application.dto.response.LoginResponse;
import com.kgu.studywithme.auth.application.dto.response.MemberInfo;
import com.kgu.studywithme.auth.application.usecase.command.OAuthLoginUseCase;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.auth.utils.OAuthProvider;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.global.exception.StudyWithMeOAuthException;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class OAuthLoginService implements OAuthLoginUseCase {
    private final List<OAuthConnector> oAuthConnectors;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenManager tokenManager;

    @Override
    public LoginResponse login(final Command command) {
        final OAuthConnector oAuthConnector = getOAuthConnectorByProvider(command.provider());
        final OAuthTokenResponse token = oAuthConnector.getToken(command.code(), command.redirectUrl());
        final OAuthUserResponse userInfo = oAuthConnector.getUserInfo(token.getAccessToken());

        final Member member = findMemberOrException(userInfo);
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        final String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        tokenManager.synchronizeRefreshToken(member.getId(), refreshToken); // sync RefreshToken

        return new LoginResponse(
                new MemberInfo(member),
                accessToken,
                refreshToken
        );
    }

    private OAuthConnector getOAuthConnectorByProvider(final OAuthProvider provider) {
        return oAuthConnectors.stream()
                .filter(oAuthConnector -> oAuthConnector.isSupported(provider))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(AuthErrorCode.INVALID_OAUTH_PROVIDER));
    }

    private Member findMemberOrException(final OAuthUserResponse userInfo) {
        return memberRepository.findByEmail(Email.from(userInfo.getEmail()))
                .orElseThrow(() -> new StudyWithMeOAuthException(userInfo));
    }
}
