package com.kgu.studywithme.member.infrastructure.query.dto;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.StudyThumbnail;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record GraduatedStudy(
        Long id,
        String name,
        String category,
        String thumbnail,
        String thumbnailBackground,
        WrittenReview review
) {
    public record WrittenReview(
            Long id,
            String content,
            LocalDateTime writtenDate,
            LocalDateTime lastModifiedDate
    ) {
    }

    @QueryProjection
    public GraduatedStudy(
            final Long id,
            final StudyName name,
            final Category category,
            final StudyThumbnail thumbnail,
            final Long reviewId,
            final String content,
            final LocalDateTime writtenDate,
            final LocalDateTime lastModifiedDate
    ) {
        this(
                id,
                name.getValue(),
                category.getName(),
                thumbnail.getImageName(),
                thumbnail.getBackground(),
                (reviewId != null)
                        ? new WrittenReview(reviewId, content, writtenDate, lastModifiedDate)
                        : null
        );
    }
}
