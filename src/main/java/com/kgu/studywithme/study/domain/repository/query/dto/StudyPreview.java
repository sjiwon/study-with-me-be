package com.kgu.studywithme.study.domain.repository.query.dto;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.study.domain.model.Capacity;
import com.kgu.studywithme.study.domain.model.Description;
import com.kgu.studywithme.study.domain.model.RecruitmentStatus;
import com.kgu.studywithme.study.domain.model.StudyName;
import com.kgu.studywithme.study.domain.model.StudyThumbnail;
import com.kgu.studywithme.study.domain.model.StudyType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudyPreview {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Thumbnail thumbnail;
    private StudyType type;
    private RecruitmentStatus recruitmentStatus;
    private int maxMember;
    private int participantMembers;
    private LocalDateTime creationDate;
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

    public record FavoriteSummary(
            Long studyId,
            Long memberId
    ) {
        @QueryProjection
        public FavoriteSummary {
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
