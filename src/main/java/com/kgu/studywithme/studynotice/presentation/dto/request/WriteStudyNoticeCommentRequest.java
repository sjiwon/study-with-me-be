package com.kgu.studywithme.studynotice.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WriteStudyNoticeCommentRequest(
        @NotBlank(message = "댓글 내용은 필수입니다.")
        String content
) {
}
