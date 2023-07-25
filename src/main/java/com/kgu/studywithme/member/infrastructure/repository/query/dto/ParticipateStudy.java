package com.kgu.studywithme.member.infrastructure.repository.query.dto;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.StudyThumbnail;
import com.querydsl.core.annotations.QueryProjection;

public record ParticipateStudy(
        Long id,
        String name,
        String category,
        String thumbnail,
        String thumbnailBackground
) {
    @QueryProjection
    public ParticipateStudy(
            final Long id,
            final StudyName name,
            final Category category,
            final StudyThumbnail thumbnail
    ) {
        this(
                id,
                name.getValue(),
                category.getName(),
                thumbnail.getImageName(),
                thumbnail.getBackground()
        );
    }
}
