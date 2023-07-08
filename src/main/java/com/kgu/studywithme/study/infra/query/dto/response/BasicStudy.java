package com.kgu.studywithme.study.infra.query.dto.response;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.domain.*;
import com.kgu.studywithme.study.domain.participant.Capacity;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class BasicStudy {
    private final Long id;
    private final String name;
    private final String description;
    private final String category;
    private final String thumbnail;
    private final String thumbnailBackground;
    private final String type;
    private final String recruitmentStatus;
    private final int currentMembers;
    private final int maxMembers;
    private final LocalDateTime registerDate;
    private List<String> hashtags;
    private List<Long> favoriteMarkingMembers;

    @QueryProjection
    public BasicStudy(
            final Long id,
            final StudyName name,
            final Description description,
            final Category category,
            final StudyThumbnail thumbnail,
            final StudyType type,
            final RecruitmentStatus recruitmentStatus,
            final int currentMembers,
            final Capacity capacity,
            final LocalDateTime registerDate
    ) {
        this.id = id;
        this.name = name.getValue();
        this.description = description.getValue();
        this.category = category.getName();
        this.thumbnail = thumbnail.getImageName();
        this.thumbnailBackground = thumbnail.getBackground();
        this.type = type.getDescription();
        this.recruitmentStatus = recruitmentStatus.getDescription();
        this.currentMembers = currentMembers + 1; // + host
        this.maxMembers = capacity.getValue();
        this.registerDate = registerDate;
    }

    public void applyHashtags(final List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void applyFavoriteMarkingMembers(final List<Long> favoriteMarkingMembers) {
        this.favoriteMarkingMembers = favoriteMarkingMembers;
    }
}
