package com.kgu.studywithme.auth.presentation.dto.response;

public record LoginResponse(
        Long id,
        String nickname,
        String email
) {
}
