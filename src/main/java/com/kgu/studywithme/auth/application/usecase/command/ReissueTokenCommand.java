package com.kgu.studywithme.auth.application.usecase.command;

public record ReissueTokenCommand(
        Long memberId,
        String refreshToken
) {
}
