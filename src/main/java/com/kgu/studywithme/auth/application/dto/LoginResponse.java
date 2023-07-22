package com.kgu.studywithme.auth.application.dto;

public record LoginResponse (
        MemberInfo member,
        String accessToken,
        String refreshToken
) {
}
