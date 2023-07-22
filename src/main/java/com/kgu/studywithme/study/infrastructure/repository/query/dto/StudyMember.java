package com.kgu.studywithme.study.infrastructure.repository.query.dto;

import com.querydsl.core.annotations.QueryProjection;

public record StudyMember(
        Long id,
        String nickname
) {
    @QueryProjection
    public StudyMember {
    }
}
