package com.kgu.studywithme.acceptance.fixture;

import com.kgu.studywithme.auth.application.dto.LoginResponse;
import com.kgu.studywithme.auth.presentation.dto.request.OAuthLoginRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.kgu.studywithme.acceptance.fixture.CommonRequestFixture.getRequest;
import static com.kgu.studywithme.acceptance.fixture.CommonRequestFixture.postRequest;
import static com.kgu.studywithme.acceptance.fixture.MemberAcceptanceFixture.회원가입을_진행한다;

public class AuthAcceptanceFixture {
    public static ValidatableResponse Google_OAuth_인증_URL를_생성한다(
            final String oAuthProvider,
            final String redirectUrl
    ) {
        final URI uri = UriComponentsBuilder
                .fromPath("/api/oauth/access/{provider}?redirectUrl={redirectUrl}")
                .build(oAuthProvider, redirectUrl);

        return getRequest(uri.getPath());
    }

    public static ValidatableResponse Google_OAuth_로그인을_진행한다(
            final String oAuthProvider,
            final String redirectUrl
    ) {
        final URI uri = UriComponentsBuilder
                .fromPath("/api/oauth/login/{provider}")
                .build(oAuthProvider);
        final OAuthLoginRequest request = new OAuthLoginRequest("JIWON", redirectUrl);

        return postRequest(request, uri.getPath());
    }

    public static String OAuth_로그인을_진행하고_AccessToken을_얻는다(
            final String oAuthProvider,
            final String redirectUrl
    ) {
        회원가입을_진행한다();

        final LoginResponse response = Google_OAuth_로그인을_진행한다(oAuthProvider, redirectUrl)
                .extract()
                .as(LoginResponse.class);

        return response.accessToken();
    }

    public static String OAuth_로그인을_진행하고_RefreshToken을_얻는다(
            final String oAuthProvider,
            final String redirectUrl
    ) {
        회원가입을_진행한다();

        final LoginResponse response = Google_OAuth_로그인을_진행한다(oAuthProvider, redirectUrl)
                .extract()
                .as(LoginResponse.class);

        return response.refreshToken();
    }

    public static ValidatableResponse 로그아웃을_진행한다(final String accessToken) {
        final URI uri = UriComponentsBuilder
                .fromPath("/api/oauth/logout")
                .build()
                .toUri();

        return postRequest(accessToken, uri.getPath());
    }

    public static ValidatableResponse 토큰을_재발급받는다(final String refreshToken) {
        final URI uri = UriComponentsBuilder
                .fromPath("/api/token/reissue")
                .build()
                .toUri();

        return postRequest(refreshToken, uri.getPath());
    }
}
