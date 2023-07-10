package com.kgu.studywithme.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OAuthLoginRequest(
        @NotBlank(message = "Authorization Code는 필수입니다.")
        String authorizationCode,

        @NotBlank(message = "Redirect Url은 필수입니다.")
        String redirectUrl
) {
}
