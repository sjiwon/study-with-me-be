package com.kgu.studywithme.study.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReviewRequest(
        @NotBlank(message = "리뷰 내용은 필수입니다.")
        String content
) {
}
