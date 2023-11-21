package com.kgu.studywithme.acceptance.auth;

import com.kgu.studywithme.auth.presentation.dto.request.OAuthLoginRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static com.kgu.studywithme.acceptance.CommonRequestFixture.getRequest;
import static com.kgu.studywithme.acceptance.CommonRequestFixture.postRequest;

public class AuthAcceptanceFixture {
    public static ValidatableResponse Google_OAuth_인증_URL를_생성한다(
            final String oAuthProvider,
            final String redirectUri
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/oauth/access/{provider}?redirectUri={redirectUri}")
                .build(oAuthProvider, redirectUri)
                .getPath();

        return getRequest(uri);
    }

    public static ValidatableResponse Google_OAuth_로그인을_진행한다(
            final String oAuthProvider,
            final String authorizationCode,
            final String redirectUri,
            final String state
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/oauth/login/{provider}")
                .build(oAuthProvider)
                .getPath();

        final OAuthLoginRequest request = new OAuthLoginRequest(authorizationCode, redirectUri, state);

        return postRequest(request, uri);
    }

    public static ValidatableResponse 로그아웃을_진행한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/oauth/logout")
                .build()
                .toUri()
                .getPath();

        return postRequest(accessToken, uri);
    }

    public static ValidatableResponse 토큰을_재발급받는다(final String accessToken, final String refreshToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/token/reissue")
                .build()
                .toUri()
                .getPath();

        return postRequest(accessToken, refreshToken, uri);
    }
}
