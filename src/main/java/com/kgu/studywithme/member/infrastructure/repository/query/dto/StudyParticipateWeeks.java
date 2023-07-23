package com.kgu.studywithme.member.infrastructure.repository.query.dto;

import com.querydsl.core.annotations.QueryProjection;

public record StudyParticipateWeeks(
        Long studyId,
        int week
) {
    @QueryProjection
    public StudyParticipateWeeks {}
}
