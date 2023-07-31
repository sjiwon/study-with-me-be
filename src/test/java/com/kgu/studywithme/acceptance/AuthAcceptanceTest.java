package com.kgu.studywithme.acceptance;

import com.kgu.studywithme.common.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.acceptance.fixture.AuthAcceptanceFixture.*;
import static com.kgu.studywithme.acceptance.fixture.MemberAcceptanceFixture.회원가입을_진행한다;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.OAuthFixture.GOOGLE_JIWON;
import static com.kgu.studywithme.common.utils.OAuthUtils.GOOGLE_PROVIDER;
import static com.kgu.studywithme.common.utils.OAuthUtils.REDIRECT_URL;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.*;

@DisplayName("[Acceptance Test] 인증 관련 기능")
public class AuthAcceptanceTest extends AcceptanceTest {
    @Test
    @DisplayName("Google OAuth 인증 Url를 요청한다")
    void queryOAuthLinkApi() {
        Google_OAuth_인증_URL를_생성한다(GOOGLE_PROVIDER, REDIRECT_URL)
                .statusCode(OK.value())
                .body("result", is("https://localhost:3000"));
    }

    @Test
    @DisplayName("DB에 이메일에 대한 사용자 정보가 없으면 OAuth UserInfo를 토대로 회원가입을 진행한다")
    void loginApiFailure() {
        Google_OAuth_로그인을_진행한다(GOOGLE_PROVIDER, GOOGLE_JIWON.getAuthorizationCode(), REDIRECT_URL)
                .statusCode(NOT_FOUND.value())
                .body("name", is(JIWON.getName()))
                .body("email", is(JIWON.getEmail().getValue()))
                .body("profileImage", is("google_profile_url"));
    }

    @Test
    @DisplayName("DB에 이메일에 대한 사용자 정보가 있으면 로그인을 진행하고 Token을 발급받는다")
    void loginApiSuccess() {
        회원가입을_진행한다(JIWON);

        Google_OAuth_로그인을_진행한다(GOOGLE_PROVIDER, GOOGLE_JIWON.getAuthorizationCode(), REDIRECT_URL)
                .statusCode(OK.value())
                .body("member.id", notNullValue(Long.class))
                .body("member.nickname", is(JIWON.getNickname().getValue()))
                .body("member.email", is(JIWON.getEmail().getValue()))
                .body("refreshToken", notNullValue(String.class))
                .body("accessToken", notNullValue(String.class));
    }

    @Test
    @DisplayName("로그아웃을 진행한다")
    void logoutApi() {
        final String accessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행한다()
                .accessToken();

        로그아웃을_진행한다(accessToken)
                .statusCode(NO_CONTENT.value());
    }

    @Test
    @DisplayName("RefreshToken을 통해서 AccessToken + RefreshToken을 재발급받는다")
    void tokenReissueApi() {
        final String refreshToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행한다()
                .refreshToken();

        토큰을_재발급받는다(refreshToken)
                .statusCode(OK.value())
                .body("refreshToken", notNullValue(String.class))
                .body("accessToken", notNullValue(String.class));
    }
}
