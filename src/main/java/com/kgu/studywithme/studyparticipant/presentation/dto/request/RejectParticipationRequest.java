package com.kgu.studywithme.studyparticipant.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RejectParticipationRequest(
        @NotBlank(message = "참여 거절 사유는 필수입니다.")
        String reason
) {
}
