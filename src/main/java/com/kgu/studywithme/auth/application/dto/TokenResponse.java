package com.kgu.studywithme.auth.application.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
