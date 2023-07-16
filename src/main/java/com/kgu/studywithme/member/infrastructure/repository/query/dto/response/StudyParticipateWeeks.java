package com.kgu.studywithme.member.infrastructure.repository.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record StudyParticipateWeeks(
        Long studyId,
        int week
) {
    @QueryProjection
    public StudyParticipateWeeks {}
}
