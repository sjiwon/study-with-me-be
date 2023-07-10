package com.kgu.studywithme.auth.utils;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {
    GOOGLE("google"),
    ;

    private final String provider;

    public static OAuthProvider of(final String provider) {
        return Arrays.stream(values())
                .filter(oAuthProvider -> oAuthProvider.provider.equals(provider))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(AuthErrorCode.INVALID_OAUTH_PROVIDER));
    }
}
