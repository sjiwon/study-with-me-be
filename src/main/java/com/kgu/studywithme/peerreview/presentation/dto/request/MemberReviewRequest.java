package com.kgu.studywithme.peerreview.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberReviewRequest(
        @NotBlank(message = "리뷰 내용은 필수입니다.")
        @Size(max = 20, message = "20자 이내로 작성해주세요.")
        String content
) {
}
