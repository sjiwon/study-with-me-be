package com.kgu.studywithme.acceptance.auth;

import com.kgu.studywithme.common.AcceptanceTest;
import com.kgu.studywithme.common.config.DatabaseCleanerEachCallbackExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.kgu.studywithme.acceptance.auth.AuthAcceptanceFixture.Google_OAuth_로그인을_진행한다;
import static com.kgu.studywithme.acceptance.auth.AuthAcceptanceFixture.Google_OAuth_인증_URL를_생성한다;
import static com.kgu.studywithme.acceptance.auth.AuthAcceptanceFixture.로그아웃을_진행한다;
import static com.kgu.studywithme.acceptance.auth.AuthAcceptanceFixture.토큰을_재발급받는다;
import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.회원가입을_진행한다;
import static com.kgu.studywithme.auth.utils.TokenResponseWriter.REFRESH_TOKEN_COOKIE;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.OAuthFixture.GOOGLE_JIWON;
import static com.kgu.studywithme.common.utils.OAuthUtils.GOOGLE_PROVIDER;
import static com.kgu.studywithme.common.utils.OAuthUtils.REDIRECT_URI;
import static com.kgu.studywithme.common.utils.OAuthUtils.STATE;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 인증 관련 기능")
public class AuthAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("OAuth 인증 URL 요청 API")
    class QueryOAuthLinkApi {
        @Test
        @DisplayName("Google OAuth 인증 URL을 요청한다")
        void success() {
            Google_OAuth_인증_URL를_생성한다(GOOGLE_PROVIDER, REDIRECT_URI)
                    .statusCode(OK.value())
                    .body("result", is("https://localhost:3000"));
        }
    }

    @Nested
    @DisplayName("로그인 API")
    class LoginApi {
        @Test
        @DisplayName("DB에 이메일에 대한 사용자 정보가 없으면 OAuth UserInfo를 토대로 회원가입을 진행한다")
        void failure() {
            Google_OAuth_로그인을_진행한다(GOOGLE_PROVIDER, GOOGLE_JIWON.getAuthorizationCode(), REDIRECT_URI, STATE)
                    .statusCode(NOT_FOUND.value())
                    .body("name", is(JIWON.getName()))
                    .body("email", is(JIWON.getEmail().getValue()));
        }

        @Test
        @DisplayName("DB에 이메일에 대한 사용자 정보가 있으면 로그인을 진행하고 Token을 발급받는다")
        void success() {
            회원가입을_진행한다(JIWON);

            Google_OAuth_로그인을_진행한다(GOOGLE_PROVIDER, GOOGLE_JIWON.getAuthorizationCode(), REDIRECT_URI, STATE)
                    .statusCode(OK.value())
                    .header(AUTHORIZATION, notNullValue(String.class))
                    .header(SET_COOKIE, notNullValue(String.class))
                    .cookie(REFRESH_TOKEN_COOKIE, notNullValue(String.class))
                    .body("id", notNullValue(Long.class))
                    .body("nickname", is(JIWON.getNickname().getValue()))
                    .body("email", is(JIWON.getEmail().getValue()));
        }
    }

    @Nested
    @DisplayName("로그아웃 API")
    class LogoutApi {
        @Test
        @DisplayName("로그아웃을 진행한다")
        void success() {
            final String accessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행하고_AccessToken을_추출한다();
            로그아웃을_진행한다(accessToken)
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("토큰 재발급 API")
    class TokenReissueApi {
        @Test
        @DisplayName("RefreshToken을 통해서 AccessToken + RefreshToken을 재발급받는다")
        void tokenReissueApi() {
            final String refreshToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행하고_RefreshToken을_추출한다();
            토큰을_재발급받는다(refreshToken)
                    .statusCode(NO_CONTENT.value())
                    .header(AUTHORIZATION, notNullValue(String.class))
                    .header(SET_COOKIE, notNullValue(String.class))
                    .cookie(REFRESH_TOKEN_COOKIE, notNullValue(String.class));
        }
    }
}
