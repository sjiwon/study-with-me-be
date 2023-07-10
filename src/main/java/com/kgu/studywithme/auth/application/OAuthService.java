package com.kgu.studywithme.auth.application;

import com.kgu.studywithme.auth.application.dto.response.LoginResponse;
import com.kgu.studywithme.auth.application.dto.response.MemberInfo;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleUserResponse;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeOAuthException;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class OAuthService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenManager tokenManager;
    private final OAuthConnector oAuthConnector;

    @StudyWithMeWritableTransactional
    public LoginResponse login(
            final String code,
            final String redirectUrl
    ) {
        final GoogleTokenResponse tokenResponse =
                (GoogleTokenResponse) oAuthConnector.getToken(code, redirectUrl);
        final GoogleUserResponse userInfo =
                (GoogleUserResponse) oAuthConnector.getUserInfo(tokenResponse.accessToken());

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

    private Member findMemberOrException(final GoogleUserResponse userInfo) {
        return memberRepository.findByEmail(Email.from(userInfo.email()))
                .orElseThrow(() -> new StudyWithMeOAuthException(userInfo));
    }

    @StudyWithMeWritableTransactional
    public void logout(final Long memberId) {
        tokenManager.deleteRefreshTokenByMemberId(memberId);
    }
}
