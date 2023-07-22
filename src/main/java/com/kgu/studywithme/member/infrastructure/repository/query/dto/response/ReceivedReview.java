package com.kgu.studywithme.member.infrastructure.repository.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record ReceivedReview(
        String content,
        LocalDateTime writtenDate
) {
    @QueryProjection
    public ReceivedReview {
    }
}
