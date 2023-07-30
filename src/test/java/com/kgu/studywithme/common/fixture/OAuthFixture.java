package com.kgu.studywithme.common.fixture;

import com.kgu.studywithme.auth.infrastructure.oauth.OAuthTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleTokenResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.utils.TokenUtils.*;

@Getter
@RequiredArgsConstructor
public enum OAuthFixture {
    GOOGLE_JIWON("JIWON", googleJiWonToken(), googleJiWonResponse()),
    ;

    private final String authorizationCode;
    private final OAuthTokenResponse oAuthTokenResponse;
    private final OAuthUserResponse oAuthUserResponse;

    private static OAuthTokenResponse googleJiWonToken() {
        return new GoogleTokenResponse(
                BEARER_TOKEN,
                ID_TOKEN,
                "JIWON_TOKEN",
                SCOPE,
                EXPIRES_IN
        );
    }

    private static OAuthUserResponse googleJiWonResponse() {
        return JIWON.toGoogleUserResponse();
    }

    public static OAuthTokenResponse parseOAuthTokenByCode(final String authorizationCode) {
        final OAuthFixture oAuthFixtures = Arrays.stream(values())
                .filter(value -> value.authorizationCode.equals(authorizationCode))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        return oAuthFixtures.oAuthTokenResponse;
    }

    public static OAuthUserResponse parseOAuthUserByAccessToken(final String accessToken) {
        final OAuthFixture oAuthFixtures = Arrays.stream(values())
                .filter(value -> value.oAuthTokenResponse.getAccessToken().equals(accessToken))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        return oAuthFixtures.oAuthUserResponse;
    }
}
