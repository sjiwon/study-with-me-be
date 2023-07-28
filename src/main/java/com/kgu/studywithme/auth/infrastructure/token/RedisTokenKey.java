package com.kgu.studywithme.auth.infrastructure.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisTokenKey {
    TOKEN_KEY("Token-%d"),
    ;

    private final String value;
}
