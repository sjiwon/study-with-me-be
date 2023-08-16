package com.kgu.studywithme.global.exception;

import com.kgu.studywithme.auth.domain.oauth.OAuthUserResponse;
import lombok.Getter;

@Getter
public class StudyWithMeOAuthException extends RuntimeException {
    private final OAuthUserResponse response;

    public StudyWithMeOAuthException(final OAuthUserResponse response) {
        super();
        this.response = response;
    }
}
