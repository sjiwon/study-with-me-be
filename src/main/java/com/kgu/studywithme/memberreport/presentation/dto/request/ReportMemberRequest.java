package com.kgu.studywithme.memberreport.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReportMemberRequest(
        @NotBlank(message = "신고 사유는 필수입니다.")
        String reason
) {
}
