package com.kgu.studywithme.memberreview.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMemberReviewRequest(
        @NotBlank(message = "수정할 리뷰 내용을 작성해주세요.")
        @Size(max = 20, message = "20자 이내로 작성해주세요.")
        String content
) {
}
