package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.study.domain.hashtag.Hashtag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.kgu.studywithme.study.domain.RecruitmentStatus.COMPLETE;
import static com.kgu.studywithme.study.domain.RecruitmentStatus.IN_PROGRESS;
import static com.kgu.studywithme.study.domain.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.StudyType.ONLINE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study")
public class Study extends BaseEntity<Study> {
    @Column(name = "host_id", nullable = false)
    private Long hostId;

    @Embedded
    private StudyName name;

    @Embedded
    private Description description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Embedded
    private Capacity capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "image", nullable = false)
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

    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    private final List<Hashtag> hashtags = new ArrayList<>();

    private Study(
            final Long hostId,
            final StudyName name,
            final Description description,
            final Capacity capacity,
            final Category category,
            final StudyThumbnail thumbnail,
            final StudyType type,
            final StudyLocation location,
            final int minimumAttendanceForGraduation,
            final Set<String> hashtags
    ) {
        this.hostId = hostId;
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.category = category;
        this.thumbnail = thumbnail;
        this.type = type;
        this.location = location;
        this.recruitmentStatus = IN_PROGRESS;
        this.graduationPolicy = GraduationPolicy.initPolicy(minimumAttendanceForGraduation);
        this.terminated = false;
        applyHashtags(hashtags);
    }

    public static Study createOnlineStudy(
            final Long hostId,
            final StudyName name,
            final Description description,
            final Capacity capacity,
            final Category category,
            final StudyThumbnail thumbnail,
            final int minimumAttendanceForGraduation,
            final Set<String> hashtags
    ) {
        return new Study(
                hostId,
                name,
                description,
                capacity,
                category,
                thumbnail,
                ONLINE,
                null,
                minimumAttendanceForGraduation,
                hashtags
        );
    }

    public static Study createOfflineStudy(
            final Long hostId,
            final StudyName name,
            final Description description,
            final Capacity capacity,
            final Category category,
            final StudyThumbnail thumbnail,
            final StudyLocation location,
            final int minimumAttendanceForGraduation,
            final Set<String> hashtags
    ) {
        return new Study(
                hostId,
                name,
                description,
                capacity,
                category,
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
            final Capacity capacity,
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
        this.capacity = capacity;
        this.category = category;
        this.thumbnail = thumbnail;
        this.type = type;
        this.location = (type == OFFLINE) ? StudyLocation.of(province, city) : null;
        this.recruitmentStatus = recruitmentStatus;
        this.graduationPolicy = this.graduationPolicy.update(minimumAttendanceForGraduation);
        applyHashtags(hashtags);
    }

    public void applyHashtags(final Set<String> hashtags) {
        this.hashtags.clear();
        if (!CollectionUtils.isEmpty(hashtags)) {
            this.hashtags.addAll(
                    hashtags.stream()
                            .map(value -> Hashtag.applyHashtag(this, value))
                            .toList()
            );
        }
    }

    public boolean isHost(final Long memberId) {
        return hostId.equals(memberId);
    }

    public void delegateHostAuthority(final Long newHostId) {
        hostId = newHostId;
        graduationPolicy = graduationPolicy.resetUpdateChanceByDelegatingHostAuthority();
    }

    public boolean isRecruitmentComplete() {
        return recruitmentStatus == COMPLETE;
    }

    public void recruitingEnd() {
        recruitmentStatus = COMPLETE;
    }

    public void terminate() {
        terminated = true;
    }

    public boolean isCapacityFull(final int currentParticipants) {
        return capacity.isFullByCompareWith(currentParticipants);
    }

    public boolean isParticipantMeetGraduationPolicy(final int attendanceCount) {
        return graduationPolicy.isGraduationRequirementsFulfilled(attendanceCount);
    }

    // Add Getter
    public String getNameValue() {
        return name.getValue();
    }

    public String getDescriptionValue() {
        return description.getValue();
    }

    public int getCapacity() {
        return capacity.getValue();
    }

    public int getMinimumAttendanceForGraduation() {
        return graduationPolicy.getMinimumAttendance();
    }

    public List<String> getHashtags() {
        return hashtags.stream()
                .map(Hashtag::getName)
                .toList();
    }
}
