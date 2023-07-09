package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.ReviewInformation;

import java.util.List;

public record ReviewAssembler(
        int graduateCount,
        List<ReviewInformation> reviews
) {
}
