package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.kgu.studywithme.study.domain.hashtag.Hashtag;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.participant.Capacity;
import com.kgu.studywithme.study.domain.participant.Participants;
import com.kgu.studywithme.study.domain.review.Review;
import com.kgu.studywithme.study.domain.review.Reviews;
import com.kgu.studywithme.study.domain.week.Period;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.Weekly;
import com.kgu.studywithme.study.domain.week.attachment.UploadAttachment;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
public class Study extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private StudyName name;

    @Embedded
    private Description description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

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
    private Participants participants;

    @Embedded
    private Weekly weekly;

    @Embedded
    private Reviews reviews;

    @Column(name = "is_closed", nullable = false)
    private boolean closed;

    @Embedded
    private GraduationPolicy graduationPolicy;

    @OneToMany(mappedBy = "study", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Hashtag> hashtags = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    private List<Attendance> attendances = new ArrayList<>();

    private Study(
            final Member host,
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
        this.name = name;
        this.description = description;
        this.category = category;
        this.thumbnail = thumbnail;
        this.type = type;
        this.location = location;
        this.recruitmentStatus = IN_PROGRESS;
        this.participants = Participants.of(host, capacity);
        this.closed = false;
        this.graduationPolicy = GraduationPolicy.initPolicy(minimumAttendanceForGraduation);
        this.weekly = Weekly.createWeeklyPage();
        this.reviews = Reviews.createReviewsPage();
        applyHashtags(hashtags);
    }

    public static Study createOnlineStudy(
            final Member host,
            final StudyName name,
            final Description description,
            final Capacity capacity,
            final Category category,
            final StudyThumbnail thumbnail,
            final int minimumAttendanceForGraduation,
            final Set<String> hashtags
    ) {
        return new Study(
                host,
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
            final Member host,
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
                host,
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
        this.participants.updateCapacity(capacity);
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
        this.hashtags.addAll(
                hashtags.stream()
                        .map(value -> Hashtag.applyHashtag(this, value))
                        .toList()
        );
    }

    public void completeRecruitment() {
        this.recruitmentStatus = COMPLETE;
    }

    public void close() {
        this.closed = true;
    }

    public void addNotice(
            final String title,
            final String content
    ) {
        notices.add(Notice.writeNotice(this, title, content));
    }

    public void recordAttendance(
            final Member participant,
            final int week,
            final AttendanceStatus status
    ) {
        validateMemberIsParticipant(participant);
        attendances.add(
                Attendance.recordAttendance(
                        this,
                        participant,
                        week,
                        status
                )
        );
    }

    public void createWeek(
            final String title,
            final String content,
            final int week,
            final Period period,
            final List<UploadAttachment> attachments
    ) {
        weekly.registerWeek(
                Week.createWeek(
                        this,
                        title,
                        content,
                        week,
                        period,
                        attachments
                )
        );
    }

    public void createWeekWithAssignment(
            final String title,
            final String content,
            final int week,
            final Period period,
            final boolean autoAttendance,
            final List<UploadAttachment> attachments
    ) {
        weekly.registerWeek(
                Week.createWeekWithAssignment(
                        this,
                        title,
                        content,
                        week,
                        period,
                        autoAttendance,
                        attachments
                )
        );
    }

    public void validateMemberIsParticipant(final Member participant) {
        participants.validateMemberIsParticipant(participant);
    }

    public void writeReview(
            final Member writer,
            final String content
    ) {
        validateMemberIsStudyGraduate(writer);
        reviews.writeReview(Review.writeReview(this, writer, content));
    }

    private void validateMemberIsStudyGraduate(final Member writer) {
        participants.validateMemberIsStudyGraduate(writer);
    }

    public void delegateStudyHostAuthority(final Member newHost) {
        validateStudyIsProceeding();
        participants.delegateStudyHostAuthority(this, newHost);
        graduationPolicy = graduationPolicy.resetUpdateChanceForDelegatingStudyHost();
    }

    public void applyParticipation(final Member member) {
        validateRecruitmentIsProceeding();
        participants.apply(this, member);
    }

    public void approveParticipation(final Member member) {
        validateStudyIsProceeding();
        participants.approve(member);
    }

    public void rejectParticipation(final Member member) {
        validateStudyIsProceeding();
        participants.reject(member);
    }

    public void cancelParticipation(final Member participant) {
        validateStudyIsProceeding();
        participants.cancel(participant);
    }

    public void graduateParticipant(final Member participant) {
        validateStudyIsProceeding();
        participants.graduate(participant);
    }

    private void validateRecruitmentIsProceeding() {
        validateStudyIsProceeding();
        validateRecruitmentStatus();
    }

    private void validateStudyIsProceeding() {
        if (closed) {
            throw StudyWithMeException.type(StudyErrorCode.ALREADY_CLOSED);
        }
    }

    private void validateRecruitmentStatus() {
        if (isRecruitmentComplete()) {
            throw StudyWithMeException.type(StudyErrorCode.RECRUITMENT_IS_COMPLETE);
        }
    }

    public boolean isRecruitmentComplete() {
        return recruitmentStatus == COMPLETE;
    }

    public void validateMemberIsApplier(final Member member) {
        participants.validateMemberIsApplier(member);
    }

    public boolean isGraduationRequirementsFulfilled(final int value) {
        return graduationPolicy.isGraduationRequirementsFulfilled(value);
    }

    // Add Getter
    public String getNameValue() {
        return name.getValue();
    }

    public String getDescriptionValue() {
        return description.getValue();
    }

    public List<String> getHashtags() {
        return hashtags.stream()
                .map(Hashtag::getName)
                .toList();
    }

    public Member getHost() {
        return participants.getHost();
    }

    public List<Member> getParticipants() {
        return participants.getParticipants();
    }

    public List<Member> getParticipantsWithoutHost() {
        return participants.getParticipantsWithoutHost();
    }

    public List<Member> getApplier() {
        return participants.getApplier();
    }

    public List<Member> getApproveParticipants() {
        return participants.getApproveParticipants();
    }

    public List<Member> getApproveParticipantsWithoutHost() {
        return participants.getApproveParticipantsWithoutHost();
    }

    public List<Member> getGraduatedParticipants() {
        return participants.getGraduatedParticipants();
    }

    public Capacity getCapacity() {
        return participants.getCapacity();
    }

    public int getMaxMembers() {
        return participants.getCapacity().getValue();
    }

    public List<Review> getReviews() {
        return reviews.getReviews();
    }

    public List<Week> getWeeks() {
        return weekly.getWeeks();
    }

    public List<Integer> getParticipantsAges() {
        return participants.getParticipantsAges();
    }

    public double getParticipantsAverageAge() {
        return participants.getParticipantsAverageAge();
    }

    public int getMinimumAttendanceForGraduation() {
        return graduationPolicy.getMinimumAttendance();
    }

    public int getRemainingOpportunityToUpdateGraduationPolicy() {
        return graduationPolicy.getUpdateChance();
    }
}
