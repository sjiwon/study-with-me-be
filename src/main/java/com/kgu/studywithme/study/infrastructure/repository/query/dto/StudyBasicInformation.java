package com.kgu.studywithme.study.infrastructure.repository.query.dto;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.domain.Gender;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.domain.Score;
import com.kgu.studywithme.study.domain.Capacity;
import com.kgu.studywithme.study.domain.Description;
import com.kgu.studywithme.study.domain.RecruitmentStatus;
import com.kgu.studywithme.study.domain.StudyLocation;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.StudyThumbnail;
import com.kgu.studywithme.study.domain.StudyType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class StudyBasicInformation {
    private final Long id;
    private final String name;
    private final String description;
    private final String category;
    private final Thumbnail thumbnail;
    private final StudyType type;
    private final StudyLocation location;
    private final RecruitmentStatus recruitmentStatus;
    private final int maxMember;
    private final int participantMembers;
    private final int minimumAttendanceForGraduation;
    private final int remainingOpportunityToUpdateGraduationPolicy;
    private final StudyMember host;
    private List<String> hashtags;
    private List<ParticipantInformation> participants;

    public record Thumbnail(
            String name,
            String background
    ) {
    }

    public record ParticipantInformation(
            Long id,
            String nickname,
            String gender,
            int score,
            int age
    ) {
        @QueryProjection
        public ParticipantInformation(
                final Long id,
                final Nickname nickname,
                final Gender gender,
                final Score score,
                final LocalDate birth
        ) {
            this(
                    id,
                    nickname.getValue(),
                    gender.getValue(),
                    score.getValue(),
                    Period.between(birth, LocalDate.now()).getYears()
            );
        }
    }

    @QueryProjection
    public StudyBasicInformation(
            final Long id,
            final StudyName name,
            final Description description,
            final Category category,
            final StudyThumbnail thumbnail,
            final StudyType type,
            final StudyLocation location,
            final RecruitmentStatus recruitmentStatus,
            final Capacity capacity,
            final int participantMembers,
            final int minimumAttendanceForGraduation,
            final int remainingOpportunityToUpdateGraduationPolicy,
            final Long hostId,
            final Nickname nickname
    ) {
        this.id = id;
        this.name = name.getValue();
        this.description = description.getValue();
        this.category = category.getName();
        this.thumbnail = new Thumbnail(thumbnail.getImageName(), thumbnail.getBackground());
        this.type = type;
        this.location = location;
        this.recruitmentStatus = recruitmentStatus;
        this.maxMember = capacity.getValue();
        this.participantMembers = participantMembers;
        this.minimumAttendanceForGraduation = minimumAttendanceForGraduation;
        this.remainingOpportunityToUpdateGraduationPolicy = remainingOpportunityToUpdateGraduationPolicy;
        this.host = new StudyMember(hostId, nickname.getValue());
    }

    public void applyHashtags(final List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void applyParticipants(final List<ParticipantInformation> participants) {
        this.participants = participants;
    }
}
