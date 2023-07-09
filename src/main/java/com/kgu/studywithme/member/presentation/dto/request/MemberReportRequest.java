package com.kgu.studywithme.member.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MemberReportRequest(
        @NotBlank(message = "신고 사유는 필수입니다.")
        String reason
) {
}
