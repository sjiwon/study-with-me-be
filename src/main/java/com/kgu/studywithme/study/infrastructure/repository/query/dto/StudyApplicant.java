package com.kgu.studywithme.study.infrastructure.repository.query.dto;

import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.domain.Score;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record StudyApplicant(
        Long id,
        String nickname,
        int score,
        LocalDateTime applyDate
) {
    @QueryProjection
    public StudyApplicant(
            final Long id,
            final Nickname nickname,
            final Score score,
            final LocalDateTime applyDate
    ) {
        this(
                id,
                nickname.getValue(),
                score.getValue(),
                applyDate
        );
    }
}
