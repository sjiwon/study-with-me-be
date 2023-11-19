package com.kgu.studywithme.studyweekly.domain.model;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "study_weekly",
        indexes = {
                @Index(name = "idx_study_weekly_end_date_is_auto_attendance_study_id_week", columnList = "end_date, is_auto_attendance, study_id, week")
        }
)
public class StudyWeekly extends BaseEntity<StudyWeekly> {
    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "week", nullable = false)
    private int week;

    @Embedded
    private Period period;

    @Column(name = "is_assignment_exists", nullable = false)
    private boolean assignmentExists;

    @Column(name = "is_auto_attendance", nullable = false)
    private boolean autoAttendance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", referencedColumnName = "id", nullable = false)
    private Member creator;

    @OneToMany(mappedBy = "weekly", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private final List<StudyWeeklyAttachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "weekly", cascade = CascadeType.PERSIST)
    private final List<StudyWeeklySubmit> submits = new ArrayList<>();

    private StudyWeekly(
            final Study study,
            final Member creator,
            final String title,
            final String content,
            final int week,
            final Period period,
            final boolean assignmentExists,
            final boolean autoAttendance,
            final List<UploadAttachment> attachments
    ) {
        this.study = study;
        this.creator = creator;
        this.title = title;
        this.content = content;
        this.week = week;
        this.period = period;
        this.assignmentExists = assignmentExists;
        this.autoAttendance = autoAttendance;
        applyAttachments(attachments);
    }

    public static StudyWeekly createWeekly(
            final Study study,
            final Member creator,
            final String title,
            final String content,
            final int week,
            final Period period,
            final List<UploadAttachment> attachments
    ) {
        return new StudyWeekly(
                study,
                creator,
                title,
                content,
                week,
                period,
                false,
                false,
                attachments
        );
    }

    public static StudyWeekly createWeeklyWithAssignment(
            final Study study,
            final Member creator,
            final String title,
            final String content,
            final int week,
            final Period period,
            final boolean autoAttendance,
            final List<UploadAttachment> attachments
    ) {
        return new StudyWeekly(
                study,
                creator,
                title,
                content,
                week,
                period,
                true,
                autoAttendance,
                attachments
        );
    }

    public void update(
            final String title,
            final String content,
            final Period period,
            final boolean assignmentExists,
            final boolean autoAttendance,
            final List<UploadAttachment> attachments
    ) {
        this.title = title;
        this.content = content;
        this.period = period;
        this.assignmentExists = assignmentExists;
        this.autoAttendance = autoAttendance;
        applyAttachments(attachments);
    }

    private void applyAttachments(final List<UploadAttachment> attachments) {
        this.attachments.clear();
        if (!CollectionUtils.isEmpty(attachments)) {
            this.attachments.addAll(
                    attachments.stream()
                            .map(attachment -> StudyWeeklyAttachment.addAttachmentFile(this, attachment))
                            .toList()
            );
        }
    }

    public void submitAssignment(final Member participant, final UploadAssignment uploadAssignment) {
        submits.add(StudyWeeklySubmit.submitAssignment(this, participant, uploadAssignment));
    }

    public boolean isSubmissionPeriodInRange(final LocalDateTime time) {
        return period.isInRange(time);
    }

    public boolean isSubmissionPeriodPassed(final LocalDateTime time) {
        return period.isPassed(time);
    }
}
