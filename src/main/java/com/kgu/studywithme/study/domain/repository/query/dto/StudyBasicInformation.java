package com.kgu.studywithme.study.domain.repository.query.dto;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.member.domain.model.Gender;
import com.kgu.studywithme.member.domain.model.Nickname;
import com.kgu.studywithme.member.domain.model.Score;
import com.kgu.studywithme.study.domain.model.Capacity;
import com.kgu.studywithme.study.domain.model.Description;
import com.kgu.studywithme.study.domain.model.RecruitmentStatus;
import com.kgu.studywithme.study.domain.model.StudyLocation;
import com.kgu.studywithme.study.domain.model.StudyName;
import com.kgu.studywithme.study.domain.model.StudyThumbnail;
import com.kgu.studywithme.study.domain.model.StudyType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class StudyBasicInformation {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Thumbnail thumbnail;
    private StudyType type;
    private StudyLocationInfo location;
    private RecruitmentStatus recruitmentStatus;
    private int maxMember;
    private int participantMembers;
    private int minimumAttendanceForGraduation;
    private int remainingOpportunityToUpdateGraduationPolicy;
    private StudyMember host;
    private List<String> hashtags;
    private List<ParticipantInformation> participants;

    public record Thumbnail(
            String name,
            String background
    ) {
    }

    public record StudyLocationInfo(
            String province,
            String city
    ) {
        public StudyLocationInfo(final StudyLocation studyLocation) {
            this(studyLocation.getProvince(), studyLocation.getCity());
        }
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
            final int currentParticipants,
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
        if (location != null) {
            this.location = new StudyLocationInfo(location);
        }
        this.recruitmentStatus = recruitmentStatus;
        this.maxMember = capacity.getValue();
        this.participantMembers = currentParticipants;
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
