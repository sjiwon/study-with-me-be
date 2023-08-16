package com.kgu.studywithme.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisTokenKey {
    REFRESH_TOKEN_KEY("RefreshToken-%d"),
    ;

    private final String value;
}