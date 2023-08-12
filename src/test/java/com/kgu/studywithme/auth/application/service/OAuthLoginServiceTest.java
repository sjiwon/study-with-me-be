package com.kgu.studywithme.auth.application.service;

import com.kgu.studywithme.auth.application.dto.LoginResponse;
import com.kgu.studywithme.auth.application.usecase.command.OAuthLoginUseCase;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.google.GoogleOAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleUserResponse;
import com.kgu.studywithme.auth.infrastructure.token.TokenPersistenceAdapter;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeOAuthException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kgu.studywithme.auth.utils.OAuthProvider.GOOGLE;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.utils.OAuthUtils.*;
import static com.kgu.studywithme.common.utils.TokenUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Auth -> OAuthLoginService 테스트")
class OAuthLoginServiceTest extends UseCaseTest {
    @InjectMocks
    private OAuthLoginService oAuthLoginService;

    @Spy
    private List<OAuthConnector> oAuthConnectors = new ArrayList<>();

    @Mock
    private GoogleOAuthConnector googleOAuthConnector;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenPersistenceAdapter tokenPersistenceAdapter;

    @BeforeEach
    void setUp() {
        oAuthConnectors.add(googleOAuthConnector);
    }

    @Nested
    @DisplayName("Google OAuth 로그인")
    class GoogleLogin {
        private final Member member = JIWON.toMember().apply(1L, LocalDateTime.now());
        private final OAuthLoginUseCase.Command command =
                new OAuthLoginUseCase.Command(
                        GOOGLE,
                        AUTHORIZATION_CODE,
                        REDIRECT_URI,
                        STATE
                );
        private final GoogleTokenResponse googleTokenResponse = createGoogleTokenResponse();
        private final GoogleUserResponse googleUserResponse = JIWON.toGoogleUserResponse();

        @Test
        @DisplayName("Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하지 않으면 예외를 발생하고 회원가입을 진행한다")
        void throwExceptionIfGoogleAuthUserNotInDB() {
            // given
            given(googleOAuthConnector.isSupported(any())).willReturn(true);
            given(googleOAuthConnector.getToken(any(), any(), any())).willReturn(googleTokenResponse);
            given(googleOAuthConnector.getUserInfo(any())).willReturn(googleUserResponse);
            given(memberRepository.findByEmail(any())).willReturn(Optional.empty());

            // when - then
            final StudyWithMeOAuthException exception = assertThrows(
                    StudyWithMeOAuthException.class,
                    () -> oAuthLoginService.login(command)
            );

            assertAll(
                    () -> assertThat(exception.getResponse())
                            .usingRecursiveComparison()
                            .isEqualTo(googleUserResponse),
                    () -> verify(googleOAuthConnector, times(1)).getToken(any(), any(), any()),
                    () -> verify(googleOAuthConnector, times(1)).getUserInfo(any()),
                    () -> verify(memberRepository, times(1)).findByEmail(any()),
                    () -> verify(jwtTokenProvider, times(0)).createAccessToken(any()),
                    () -> verify(jwtTokenProvider, times(0)).createRefreshToken(any()),
                    () -> verify(tokenPersistenceAdapter, times(0)).synchronizeRefreshToken(any(), any())
            );
        }

        @Test
        @DisplayName("Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하면 로그인에 성공하고 사용자 정보 및 토큰을 발급해준다")
        void success() {
            // given
            given(googleOAuthConnector.isSupported(any())).willReturn(true);
            given(googleOAuthConnector.getToken(any(), any(), any())).willReturn(googleTokenResponse);
            given(googleOAuthConnector.getUserInfo(any())).willReturn(googleUserResponse);
            given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
            given(jwtTokenProvider.createAccessToken(any())).willReturn(ACCESS_TOKEN);
            given(jwtTokenProvider.createRefreshToken(any())).willReturn(REFRESH_TOKEN);

            // when
            final LoginResponse response = oAuthLoginService.login(command);

            // then
            assertAll(
                    () -> verify(googleOAuthConnector, times(1)).getToken(any(), any(), any()),
                    () -> verify(googleOAuthConnector, times(1)).getUserInfo(any()),
                    () -> verify(memberRepository, times(1)).findByEmail(any()),
                    () -> verify(jwtTokenProvider, times(1)).createAccessToken(any()),
                    () -> verify(jwtTokenProvider, times(1)).createRefreshToken(any()),
                    () -> verify(tokenPersistenceAdapter, times(1)).synchronizeRefreshToken(any(), any()),
                    () -> assertThat(response.accessToken()).isEqualTo(ACCESS_TOKEN),
                    () -> assertThat(response.refreshToken()).isEqualTo(REFRESH_TOKEN)
            );
        }
    }
}
