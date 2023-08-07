package com.kgu.studywithme.studyweekly.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record SubmitWeeklyAssignmentRequest(
        @NotBlank(message = "과제 제출 타입은 필수입니다.")
        String type,
        MultipartFile file,
        String link
) {
}
