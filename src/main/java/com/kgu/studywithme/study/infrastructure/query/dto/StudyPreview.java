package com.kgu.studywithme.study.infrastructure.query.dto;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.domain.Capacity;
import com.kgu.studywithme.study.domain.Description;
import com.kgu.studywithme.study.domain.RecruitmentStatus;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.StudyThumbnail;
import com.kgu.studywithme.study.domain.StudyType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class StudyPreview {
    private final Long id;
    private final String name;
    private final String description;
    private final String category;
    private final Thumbnail thumbnail;
    private final StudyType type;
    private final RecruitmentStatus recruitmentStatus;
    private final int maxMember;
    private final int participantMembers;
    private final LocalDateTime creationDate;
    private List<String> hashtags;
    private List<Long> likeMarkingMembers;

    public record Thumbnail(
            String name,
            String background
    ) {
    }

    public record HashtagSummary(
            Long studyId,
            String value
    ) {
        @QueryProjection
        public HashtagSummary {
        }
    }

    @QueryProjection
    public StudyPreview(
            final Long id,
            final StudyName name,
            final Description description,
            final Category category,
            final StudyThumbnail thumbnail,
            final StudyType type,
            final RecruitmentStatus recruitmentStatus,
            final Capacity capacity,
            final int currentParticipants,
            final LocalDateTime creationDate
    ) {
        this.id = id;
        this.name = name.getValue();
        this.description = description.getValue();
        this.category = category.getName();
        this.thumbnail = new Thumbnail(thumbnail.getImageName(), thumbnail.getBackground());
        this.type = type;
        this.recruitmentStatus = recruitmentStatus;
        this.maxMember = capacity.getValue();
        this.participantMembers = currentParticipants;
        this.creationDate = creationDate;
    }

    public void applyHashtags(final List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void applyLikeMarkingMembers(final List<Long> likeMarkingMembers) {
        this.likeMarkingMembers = likeMarkingMembers;
    }
}