package com.kgu.studywithme.auth.application.dto.response;

public record LoginResponse (
        MemberInfo member,
        String accessToken,
        String refreshToken
) {
}
