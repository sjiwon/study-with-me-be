package com.kgu.studywithme.auth.application;

import com.kgu.studywithme.auth.application.dto.response.LoginResponse;
import com.kgu.studywithme.auth.application.usecase.command.OAuthLoginUseCase;
import com.kgu.studywithme.auth.infrastructure.oauth.google.GoogleOAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleUserResponse;
import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeOAuthException;
import com.kgu.studywithme.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static com.kgu.studywithme.auth.utils.OAuthProvider.GOOGLE;
import static com.kgu.studywithme.common.utils.TokenUtils.createGoogleTokenResponse;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("Auth [Application Layer] -> OAuthLoginService 테스트")
class OAuthLoginServiceTest extends ServiceTest {
    @Autowired
    private OAuthLoginService oAuthLoginService;

    @MockBean
    private GoogleOAuthConnector googleOAuthConnector;

    @Nested
    @DisplayName("Google OAuth 로그인")
    class googleLogin {
        @Test
        @DisplayName("Google OAuth 인증을 진행할 때 해당 사용자가 DB에 존재하지 않으면 예외를 발생하고 로그인에 실패한다")
        void throwExceptionIfGoogleAuthUserNotInDB() {
            // given
            final GoogleTokenResponse googleTokenResponse = createGoogleTokenResponse();
            final GoogleUserResponse googleUserResponse = JIWON.toGoogleUserResponse();

            given(googleOAuthConnector.isSupported(any())).willReturn(true);
            given(googleOAuthConnector.getToken(any(), any())).willReturn(googleTokenResponse);
            given(googleOAuthConnector.getUserInfo(any())).willReturn(googleUserResponse);

            // when - then
            StudyWithMeOAuthException exception = assertThrows(
                    StudyWithMeOAuthException.class,
                    () -> oAuthLoginService.login(
                            new OAuthLoginUseCase.Command(
                                    GOOGLE,
                                    UUID.randomUUID().toString(),
                                    "http://localhost:3000"
                            )
                    )
            );

            assertThat(exception.getResponse())
                    .usingRecursiveComparison()
                    .isEqualTo(googleUserResponse);
        }

        @Test
        @DisplayName("Google OAuth 인증을 진행할 때 해당 사용자가 DB에 존재하면 로그인에 성공하고 사용자 정보 및 토큰을 발급해준다")
        void success() {
            // given
            final Member member = memberRepository.save(JIWON.toMember());
            final GoogleTokenResponse googleTokenResponse = createGoogleTokenResponse();
            final GoogleUserResponse googleUserResponse = JIWON.toGoogleUserResponse();

            given(googleOAuthConnector.isSupported(any())).willReturn(true);
            given(googleOAuthConnector.getToken(any(), any())).willReturn(googleTokenResponse);
            given(googleOAuthConnector.getUserInfo(any())).willReturn(googleUserResponse);

            // when
            LoginResponse tokenResponse = oAuthLoginService.login(
                    new OAuthLoginUseCase.Command(
                            GOOGLE,
                            UUID.randomUUID().toString(),
                            "http://localhost:3000"
                    )
            );

            // then
            assertAll(
                    () -> assertThat(tokenResponse).isNotNull(),
                    () -> assertThat(tokenResponse)
                            .usingRecursiveComparison()
                            .isNotNull(),
                    () -> assertThat(jwtTokenProvider.getId(tokenResponse.accessToken())).isEqualTo(member.getId()),
                    () -> assertThat(jwtTokenProvider.getId(tokenResponse.refreshToken())).isEqualTo(member.getId()),
                    () -> assertThat(
                            tokenRepository.findByMemberId(member.getId())
                                    .orElseThrow()
                                    .getRefreshToken()
                    ).isEqualTo(tokenResponse.refreshToken())
            );
        }
    }
}
