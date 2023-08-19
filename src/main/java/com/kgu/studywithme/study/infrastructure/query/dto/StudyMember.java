package com.kgu.studywithme.study.infrastructure.query.dto;

import com.kgu.studywithme.member.domain.Nickname;
import com.querydsl.core.annotations.QueryProjection;

public record StudyMember(
        Long id,
        String nickname
) {
    @QueryProjection
    public StudyMember(
            final Long id,
            final Nickname nickname
    ) {
        this(
                id,
                nickname.getValue()
        );
    }
}
