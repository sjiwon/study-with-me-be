package com.kgu.studywithme.member.domain.repository.query.dto;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.study.domain.model.StudyName;
import com.kgu.studywithme.study.domain.model.StudyThumbnail;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
public class GraduatedStudy {
    private final Long id;
    private final String name;
    private final String category;
    private final String thumbnail;
    private final String thumbnailBackground;
    private WrittenReview review;

    @QueryProjection
    public GraduatedStudy(
            final Long id,
            final StudyName name,
            final Category category,
            final StudyThumbnail thumbnail
    ) {
        this.id = id;
        this.name = name.getValue();
        this.category = category.getName();
        this.thumbnail = thumbnail.getImageName();
        this.thumbnailBackground = thumbnail.getBackground();
    }

    public record WrittenReview(
            Long id,
            String content,
            LocalDateTime writtenDate,
            LocalDateTime lastModifiedDate
    ) {
    }

    public void applyWrittenReview(final WrittenReview review) {
        this.review = review;
    }
}
