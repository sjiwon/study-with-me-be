package com.kgu.studywithme.study.controller.dto.request;

import com.kgu.studywithme.study.utils.validator.ValidUploadType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record WeeklyAssignmentSubmitRequest(
        @ValidUploadType
        @NotBlank(message = "과제 제출 타입은 필수입니다.")
        String type,
        MultipartFile file,
        String link
) {
}
