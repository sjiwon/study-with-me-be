package com.kgu.studywithme.auth.application.usecase;

import com.kgu.studywithme.auth.application.adapter.OAuthConnector;
import com.kgu.studywithme.auth.application.usecase.command.OAuthLoginCommand;
import com.kgu.studywithme.auth.domain.model.AuthMember;
import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.domain.service.TokenIssuer;
import com.kgu.studywithme.auth.infrastructure.oauth.google.GoogleOAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleUserResponse;
import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeOAuthException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.kgu.studywithme.auth.domain.model.oauth.OAuthProvider.GOOGLE;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.utils.OAuthUtils.AUTHORIZATION_CODE;
import static com.kgu.studywithme.common.utils.OAuthUtils.REDIRECT_URI;
import static com.kgu.studywithme.common.utils.OAuthUtils.STATE;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.createGoogleTokenResponse;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Auth -> OAuthLoginUseCase 테스트")
class OAuthLoginUseCaseTest extends UseCaseTest {
    private final GoogleOAuthConnector googleOAuthConnector = mock(GoogleOAuthConnector.class);
    private final List<OAuthConnector> oAuthConnectors = List.of(googleOAuthConnector);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final TokenIssuer tokenIssuer = mock(TokenIssuer.class);
    private final OAuthLoginUseCase sut = new OAuthLoginUseCase(oAuthConnectors, memberRepository, tokenIssuer);

    @Nested
    @DisplayName("Google OAuth 로그인")
    class GoogleLogin {
        private final Member member = JIWON.toMember().apply(1L);
        private final OAuthLoginCommand command = new OAuthLoginCommand(GOOGLE, AUTHORIZATION_CODE, REDIRECT_URI, STATE);
        private final GoogleTokenResponse googleTokenResponse = createGoogleTokenResponse();
        private final GoogleUserResponse googleUserResponse = JIWON.toGoogleUserResponse();

        @Test
        @DisplayName("Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하지 않으면 예외를 발생하고 회원가입을 진행한다")
        void throwExceptionIfGoogleAuthUserNotInDB() {
            // given
            given(googleOAuthConnector.isSupported(GOOGLE)).willReturn(true);
            given(googleOAuthConnector.fetchToken(AUTHORIZATION_CODE, REDIRECT_URI, STATE)).willReturn(googleTokenResponse);
            given(googleOAuthConnector.fetchUserInfo(googleTokenResponse.accessToken())).willReturn(googleUserResponse);
            given(memberRepository.findByEmail(googleUserResponse.email())).willReturn(Optional.empty());

            // when - then
            final StudyWithMeOAuthException exception = assertThrows(
                    StudyWithMeOAuthException.class,
                    () -> sut.invoke(command)
            );

            assertAll(
                    () -> assertThat(exception.getResponse())
                            .usingRecursiveComparison()
                            .isEqualTo(googleUserResponse),
                    () -> verify(googleOAuthConnector, times(1)).fetchToken(AUTHORIZATION_CODE, REDIRECT_URI, STATE),
                    () -> verify(googleOAuthConnector, times(1)).fetchUserInfo(googleTokenResponse.accessToken()),
                    () -> verify(memberRepository, times(1)).findByEmail(googleUserResponse.email()),
                    () -> verify(tokenIssuer, times(0)).provideAuthorityToken(member.getId())
            );
        }

        @Test
        @DisplayName("Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하면 로그인에 성공하고 사용자 정보 및 토큰을 발급해준다")
        void success() {
            // given
            given(googleOAuthConnector.isSupported(GOOGLE)).willReturn(true);
            given(googleOAuthConnector.fetchToken(AUTHORIZATION_CODE, REDIRECT_URI, STATE)).willReturn(googleTokenResponse);
            given(googleOAuthConnector.fetchUserInfo(googleTokenResponse.accessToken())).willReturn(googleUserResponse);
            given(memberRepository.findByEmail(googleUserResponse.email())).willReturn(Optional.of(member));

            final AuthToken authToken = new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN);
            given(tokenIssuer.provideAuthorityToken(member.getId())).willReturn(authToken);

            // when
            final AuthMember response = sut.invoke(command);

            // then
            assertAll(
                    () -> verify(googleOAuthConnector, times(1)).fetchToken(AUTHORIZATION_CODE, REDIRECT_URI, STATE),
                    () -> verify(googleOAuthConnector, times(1)).fetchUserInfo(googleTokenResponse.accessToken()),
                    () -> verify(memberRepository, times(1)).findByEmail(googleUserResponse.email()),
                    () -> verify(tokenIssuer, times(1)).provideAuthorityToken(member.getId()),
                    () -> assertThat(response.member().id()).isEqualTo(member.getId()),
                    () -> assertThat(response.member().nickname()).isEqualTo(member.getNickname().getValue()),
                    () -> assertThat(response.member().email()).isEqualTo(member.getEmail().getValue()),
                    () -> assertThat(response.token().accessToken()).isEqualTo(authToken.accessToken()),
                    () -> assertThat(response.token().refreshToken()).isEqualTo(authToken.refreshToken())
            );
        }
    }
}
