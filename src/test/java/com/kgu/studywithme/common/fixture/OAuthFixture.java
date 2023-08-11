package com.kgu.studywithme.common.fixture;

import com.kgu.studywithme.auth.infrastructure.oauth.OAuthTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleTokenResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static com.kgu.studywithme.common.fixture.MemberFixture.*;
import static com.kgu.studywithme.common.utils.TokenUtils.*;

@Getter
@RequiredArgsConstructor
public enum OAuthFixture {
    GOOGLE_JIWON(
            JIWON.getEmail().getValue(), "JIWON",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "JIWON_TOKEN",
                    SCOPE,
                    EXPIRES_IN
            ), JIWON.toGoogleUserResponse()
    ),
    GOOGLE_GHOST(
            GHOST.getEmail().getValue(), "GHOST",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "GHOST_TOKEN",
                    SCOPE,
                    EXPIRES_IN
            ), GHOST.toGoogleUserResponse()
    ),
    GOOGLE_ANONYMOUS(
            ANONYMOUS.getEmail().getValue(), "ANONYMOUS",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "ANONYMOUS_TOKEN",
                    SCOPE,
                    EXPIRES_IN
            ), ANONYMOUS.toGoogleUserResponse()
    ),
    GOOGLE_DUMMY1(
            DUMMY1.getEmail().getValue(), "DUMMY1",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "DUMMY1_TOKEN",
                    SCOPE,
                    EXPIRES_IN
            ), DUMMY1.toGoogleUserResponse()
    ),
    GOOGLE_DUMMY2(
            DUMMY2.getEmail().getValue(), "DUMMY2",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "DUMMY2_TOKEN",
                    SCOPE,
                    EXPIRES_IN
            ), DUMMY2.toGoogleUserResponse()
    ),
    GOOGLE_DUMMY3(
            DUMMY3.getEmail().getValue(), "DUMMY3",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "DUMMY3_TOKEN",
                    SCOPE,
                    EXPIRES_IN
            ), DUMMY3.toGoogleUserResponse()
    ),
    GOOGLE_DUMMY4(
            DUMMY4.getEmail().getValue(), "DUMMY4",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "DUMMY4_TOKEN",
                    SCOPE,
                    EXPIRES_IN
            ), DUMMY4.toGoogleUserResponse()
    ),
    GOOGLE_DUMMY5(
            DUMMY5.getEmail().getValue(), "DUMMY5",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "DUMMY5_TOKEN",
                    SCOPE,
                    EXPIRES_IN
            ), DUMMY5.toGoogleUserResponse()
    ),
    GOOGLE_DUMMY6(
            DUMMY6.getEmail().getValue(), "ANONYMOUS",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "DUMMY6_TOKEN",
                    SCOPE,
                    EXPIRES_IN
            ), DUMMY6.toGoogleUserResponse()
    ),
    GOOGLE_DUMMY7(
            DUMMY7.getEmail().getValue(), "DUMMY7",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "DUMMY7_TOKEN",
                    SCOPE,
                    EXPIRES_IN
            ), DUMMY7.toGoogleUserResponse()
    ),
    GOOGLE_DUMMY8(
            DUMMY8.getEmail().getValue(), "DUMMY8",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "DUMMY8_TOKEN",
                    SCOPE,
                    EXPIRES_IN
            ), DUMMY8.toGoogleUserResponse()
    ),
    GOOGLE_DUMMY9(
            DUMMY9.getEmail().getValue(), "DUMMY9",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "DUMMY9_TOKEN",
                    SCOPE,
                    EXPIRES_IN
            ), DUMMY9.toGoogleUserResponse()
    ),
    ;

    private final String identifier;
    private final String authorizationCode;
    private final OAuthTokenResponse oAuthTokenResponse;
    private final OAuthUserResponse oAuthUserResponse;

    public static String getAuthorizationCodeByIdentifier(final String identifier) {
        return Arrays.stream(values())
                .filter(value -> value.identifier.equals(identifier))
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .authorizationCode;
    }

    public static OAuthTokenResponse parseOAuthTokenByCode(final String authorizationCode) {
        return Arrays.stream(values())
                .filter(value -> value.authorizationCode.equals(authorizationCode))
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .oAuthTokenResponse;
    }

    public static OAuthUserResponse parseOAuthUserByAccessToken(final String accessToken) {
        return Arrays.stream(values())
                .filter(value -> value.oAuthTokenResponse.accessToken().equals(accessToken))
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .oAuthUserResponse;
    }
}
