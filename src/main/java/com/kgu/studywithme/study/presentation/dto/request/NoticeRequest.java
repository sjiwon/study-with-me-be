package com.kgu.studywithme.study.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record NoticeRequest(
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @NotBlank(message = "내용은 필수입니다.")
        String content
) {
}