package com.kgu.studywithme.study.infrastructure.query.dto;

import com.kgu.studywithme.member.domain.Nickname;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewInformation(
        List<ReviewMetadata> reviews,
        int graduateCount
) {
    public record ReviewMetadata(
            Long id,
            String content,
            LocalDateTime writtenDate,
            StudyMember reviewer
    ) {
        @QueryProjection
        public ReviewMetadata(
                final Long id,
                final String content,
                final LocalDateTime writtenDate,
                final Long reviewerId,
                final Nickname reviewerNickname
        ) {
            this(
                    id,
                    content,
                    writtenDate,
                    new StudyMember(reviewerId, reviewerNickname.getValue())
            );
        }
    }
}
