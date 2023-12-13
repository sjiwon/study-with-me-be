package com.kgu.studywithme.study.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GetStudiesByRecommendRequest(
        @NotBlank(message = "정렬 조건은 필수입니다.")
        String sort,

        @NotNull(message = "현재 페이지는 필수입니다.")
        @Min(0)
        Integer page,

        String type,
        String province,
        String city
) {
}
