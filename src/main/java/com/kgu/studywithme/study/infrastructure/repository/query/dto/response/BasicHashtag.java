package com.kgu.studywithme.study.infrastructure.repository.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record BasicHashtag(
        Long studyId,
        String name
) {
    @QueryProjection
    public BasicHashtag {}
}
