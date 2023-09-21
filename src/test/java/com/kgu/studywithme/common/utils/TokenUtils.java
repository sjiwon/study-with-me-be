package com.kgu.studywithme.common.utils;

import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleTokenResponse;

public class TokenUtils {
    public static final String BEARER_TOKEN = "Bearer";
    public static final String ID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiaWF0IjoxNjc3OTM3MjI0LCJleHAiOjE2Nzc5NDQ0MjR9.t61tw4gDEBuXBn_DnCwiPIDaI-KcN9Zkn3QJSEK7fag";
    public static final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiaWF0IjoxNjc3OTM3MjI0LCJleHAiOjE2Nzc5NDQ0MjR9.t61tw4gDEBuXBn_DnCwiPIDaI-KcN9Zkn3QJSEK7fag";
    public static final String REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiaWF0IjoxNjc3OTM3MjI0LCJleHAiOjE2Nzg1NDIwMjR9.doqGa5Hcq6chjER1y5brJEv81z0njcJqeYxJb159ZX4";
    public static final long EXPIRES_IN = 3000;

    public static String applyAccessTokenToAuthorizationHeader() {
        return String.join(" ", BEARER_TOKEN, ACCESS_TOKEN);
    }

    public static String applyRefreshTokenToAuthorizationHeader() {
        return String.join(" ", BEARER_TOKEN, REFRESH_TOKEN);
    }

    public static AuthToken createTokenResponse() {
        return new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN);
    }

    public static GoogleTokenResponse createGoogleTokenResponse() {
        return new GoogleTokenResponse(
                BEARER_TOKEN,
                ID_TOKEN,
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN
        );
    }
}
