package com.kgu.studywithme.member.domain.repository.query.dto;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.study.domain.model.StudyName;
import com.kgu.studywithme.study.domain.model.StudyThumbnail;
import com.querydsl.core.annotations.QueryProjection;

public record LikeMarkedStudy(
        Long id,
        String name,
        String category,
        String thumbnail,
        String thumbnailBackground
) {
    @QueryProjection
    public LikeMarkedStudy(
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
