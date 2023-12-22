package com.kgu.studywithme.auth.application.usecase.command;

public record ReissueTokenCommand(
        String refreshToken
) {
}
