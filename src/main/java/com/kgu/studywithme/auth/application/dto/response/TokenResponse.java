package com.kgu.studywithme.auth.application.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
