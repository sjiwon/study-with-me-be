package com.kgu.studywithme.study.domain.model;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

import static com.kgu.studywithme.study.domain.model.RecruitmentStatus.OFF;
import static com.kgu.studywithme.study.domain.model.RecruitmentStatus.ON;
import static com.kgu.studywithme.study.domain.model.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.model.StudyType.ONLINE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "study",
        indexes = {
                @Index(name = "idx_study_category_is_terminated", columnList = "category, is_terminated"),
                @Index(name = "idx_study_study_type_category_is_terminated", columnList = "study_type, category, is_terminated"),
                @Index(name = "idx_study_province_city_category_is_terminated", columnList = "province, city, category, is_terminated"),
                @Index(name = "idx_study_province_city_study_type_category_is_terminated", columnList = "province, city, study_type, category, is_terminated")
        }
)
public class Study extends BaseEntity<Study> {
    @Embedded
    private StudyName name;

    @Embedded
    private Description description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Embedded
    private Capacity capacity;

    @Column(name = "participants", nullable = false)
    private int participants;

    @Enumerated(EnumType.STRING)
    @Column(name = "thumbnail", nullable = false)
    private StudyThumbnail thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(name = "study_type", nullable = false, updatable = false)
    private StudyType type;

    @Embedded
    private StudyLocation location;

    @Enumerated(EnumType.STRING)
    @Column(name = "recruitment_status", nullable = false)
    private RecruitmentStatus recruitmentStatus;

    @Embedded
    private GraduationPolicy graduationPolicy;

    @Column(name = "is_terminated", nullable = false)
    private boolean terminated;

    @Embedded
    private Hashtags hashtags;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "host_id", referencedColumnName = "id", nullable = false)
    private Member host;

    // Denormalization
    @Column(name = "favorite_count", nullable = false)
    private int favoriteCount;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    private Study(
            final Member host,
            final StudyName name,
            final Description description,
            final Category category,
            final Capacity capacity,
            final StudyThumbnail thumbnail,
            final StudyType type,
            final StudyLocation location,
            final int minimumAttendanceForGraduation,
            final Set<String> hashtags
    ) {
        this.host = host;
        this.name = name;
        this.description = description;
        this.category = category;
        this.capacity = capacity;
        this.participants = 1; // host
        this.thumbnail = thumbnail;
        this.type = type;
        this.location = location;
        this.recruitmentStatus = ON;
        this.graduationPolicy = GraduationPolicy.initPolicy(minimumAttendanceForGraduation);
        this.terminated = false;
        this.hashtags = new Hashtags(this, hashtags);
        this.favoriteCount = 0;
        this.reviewCount = 0;
    }

    public static Study createOnlineStudy(
            final Member host,
            final StudyName name,
            final Description description,
            final Category category,
            final Capacity capacity,
            final StudyThumbnail thumbnail,
            final int minimumAttendanceForGraduation,
            final Set<String> hashtags
    ) {
        return new Study(
                host,
                name,
                description,
                category,
                capacity,
                thumbnail,
                ONLINE,
                null,
                minimumAttendanceForGraduation,
                hashtags
        );
    }

    public static Study createOfflineStudy(
            final Member host,
            final StudyName name,
            final Description description,
            final Category category,
            final Capacity capacity,
            final StudyThumbnail thumbnail,
            final StudyLocation location,
            final int minimumAttendanceForGraduation,
            final Set<String> hashtags
    ) {
        return new Study(
                host,
                name,
                description,
                category,
                capacity,
                thumbnail,
                OFFLINE,
                location,
                minimumAttendanceForGraduation,
                hashtags
        );
    }

    public void update(
            final StudyName name,
            final Description description,
            final int capacity,
            final Category category,
            final StudyThumbnail thumbnail,
            final StudyType type,
            final String province,
            final String city,
            final RecruitmentStatus recruitmentStatus,
            final int minimumAttendanceForGraduation,
            final Set<String> hashtags
    ) {
        this.name = name;
        this.description = description;
        this.capacity = this.capacity.update(capacity, participants);
        this.category = category;
        this.thumbnail = thumbnail;
        this.type = type;
        this.location = (type == OFFLINE) ? new StudyLocation(province, city) : null;
        this.recruitmentStatus = recruitmentStatus;
        this.graduationPolicy = this.graduationPolicy.update(minimumAttendanceForGraduation);
        this.hashtags.update(this, hashtags);
    }

    public boolean isHost(final Member other) {
        return host.isSameMember(other);
    }

    public void delegateHostAuthority(final Member newHost) {
        host = newHost;
        graduationPolicy = graduationPolicy.resetUpdateChance();
    }

    public void recruitmentOff() {
        recruitmentStatus = OFF;
    }

    public void terminate() {
        terminated = true;
    }

    public void addParticipant() {
        validateStudyCapacityIsAvailable();
        participants++;
    }

    private void validateStudyCapacityIsAvailable() {
        if (capacity.isFull(participants)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.STUDY_CAPACITY_ALREADY_FULL);
        }
    }

    public void removeParticipant() {
        participants--;
    }

    public boolean isParticipantMeetGraduationPolicy(final int attendanceCount) {
        return graduationPolicy.isGraduationRequirementsFulfilled(attendanceCount);
    }

    public void increaseFavoriteCount() {
        favoriteCount++;
    }

    public void decreaseFavoriteCount() {
        favoriteCount--;
    }

    public void increaseReviewCount() {
        reviewCount++;
    }

    public void decreaseReviewCount() {
        reviewCount--;
    }

    public List<String> getHashtags() {
        return hashtags.getHashtags()
                .stream()
                .map(Hashtag::getName)
                .toList();
    }
}
