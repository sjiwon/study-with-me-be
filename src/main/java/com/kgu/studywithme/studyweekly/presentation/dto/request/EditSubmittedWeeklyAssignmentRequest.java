package com.kgu.studywithme.studyweekly.presentation.dto.request;

import com.kgu.studywithme.studyweekly.utils.validator.ValidUploadType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record EditSubmittedWeeklyAssignmentRequest(
        @ValidUploadType
        @NotBlank(message = "과제 제출 타입은 필수입니다.")
        String type,
        MultipartFile file,
        String link
) {
}
