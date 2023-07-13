package com.kgu.studywithme.auth.application.service;

import com.kgu.studywithme.auth.application.dto.response.LoginResponse;
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
import java.util.UUID;

import static com.kgu.studywithme.auth.utils.OAuthProvider.GOOGLE;
import static com.kgu.studywithme.common.utils.TokenUtils.*;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
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
    class googleLogin {
        private final Member member = JIWON.toMember().apply(1L, LocalDateTime.now());
        private final OAuthLoginUseCase.Command command = new OAuthLoginUseCase.Command(
                GOOGLE,
                UUID.randomUUID().toString(),
                "http://localhost:3000"
        );

        @Test
        @DisplayName("Google OAuth 인증을 진행할 때 해당 사용자가 DB에 존재하지 않으면 예외를 발생하고 로그인에 실패한다")
        void throwExceptionIfGoogleAuthUserNotInDB() {
            // given
            final GoogleTokenResponse googleTokenResponse = createGoogleTokenResponse();
            final GoogleUserResponse googleUserResponse = JIWON.toGoogleUserResponse();

            given(googleOAuthConnector.isSupported(any())).willReturn(true);
            given(googleOAuthConnector.getToken(any(), any())).willReturn(googleTokenResponse);
            given(googleOAuthConnector.getUserInfo(any())).willReturn(googleUserResponse);
            given(memberRepository.findByEmail(any())).willReturn(Optional.empty());

            // when - then
            StudyWithMeOAuthException exception = assertThrows(
                    StudyWithMeOAuthException.class,
                    () -> oAuthLoginService.login(command)
            );

            verify(jwtTokenProvider, times(0)).createAccessToken(member.getId());
            verify(jwtTokenProvider, times(0)).createRefreshToken(member.getId());
            verify(tokenPersistenceAdapter, times(0))
                    .synchronizeRefreshToken(member.getId(), REFRESH_TOKEN);

            assertThat(exception.getResponse())
                    .usingRecursiveComparison()
                    .isEqualTo(googleUserResponse);
        }

        @Test
        @DisplayName("Google OAuth 인증을 진행할 때 해당 사용자가 DB에 존재하면 로그인에 성공하고 사용자 정보 및 토큰을 발급해준다")
        void success() {
            // given
            final GoogleTokenResponse googleTokenResponse = createGoogleTokenResponse();
            final GoogleUserResponse googleUserResponse = JIWON.toGoogleUserResponse();

            given(googleOAuthConnector.isSupported(any())).willReturn(true);
            given(googleOAuthConnector.getToken(any(), any())).willReturn(googleTokenResponse);
            given(googleOAuthConnector.getUserInfo(any())).willReturn(googleUserResponse);
            given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
            given(jwtTokenProvider.createAccessToken(any())).willReturn(ACCESS_TOKEN);
            given(jwtTokenProvider.createRefreshToken(any())).willReturn(REFRESH_TOKEN);

            // when
            LoginResponse response = oAuthLoginService.login(command);

            // then
            verify(jwtTokenProvider, times(1)).createAccessToken(member.getId());
            verify(jwtTokenProvider, times(1)).createRefreshToken(member.getId());
            verify(tokenPersistenceAdapter, times(1))
                    .synchronizeRefreshToken(member.getId(), REFRESH_TOKEN);

            assertAll(
                    () -> assertThat(response.accessToken()).isEqualTo(ACCESS_TOKEN),
                    () -> assertThat(response.refreshToken()).isEqualTo(REFRESH_TOKEN)
            );
        }
    }
}
