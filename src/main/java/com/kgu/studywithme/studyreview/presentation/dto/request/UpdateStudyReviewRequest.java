package com.kgu.studywithme.studyreview.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateStudyReviewRequest(
        @NotBlank(message = "리뷰 내용은 필수입니다.")
        String content
) {
}
