package com.kgu.studywithme.study.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Set;

public record CreateStudyRequest(
        @NotBlank(message = "스터디명은 필수입니다.")
        String name,

        @NotBlank(message = "스터디 설명은 필수입니다.")
        String description,

        @NotNull(message = "참여인원은 필수입니다.")
        Integer capacity,

        @NotNull(message = "카테고리는 필수입니다.")
        Long category,

        @NotBlank(message = "스터디 썸네일은 필수입니다.")
        String thumbnail,

        @NotBlank(message = "온/오프라인 유무는 필수입니다.")
        String type,

        String province,

        String city,

        @NotNull(message = "스터디 졸업요건은 필수입니다. [최소 출석 횟수]")
        @Positive(message = "졸업 요건은 양수여야 합니다.")
        Integer minimumAttendanceForGraduation,

        @NotEmpty(message = "해시태그는 하나 이상 등록해야 합니다.")
        Set<String> hashtags
) {
}
