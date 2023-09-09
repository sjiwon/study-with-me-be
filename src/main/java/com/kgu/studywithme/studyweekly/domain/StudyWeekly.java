package com.kgu.studywithme.studyweekly.domain;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.studyweekly.domain.attachment.StudyWeeklyAttachment;
import com.kgu.studywithme.studyweekly.domain.attachment.UploadAttachment;
import com.kgu.studywithme.studyweekly.domain.submit.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_weekly")
public class StudyWeekly extends BaseEntity<StudyWeekly> {
    @Column(name = "study_id", nullable = false)
    private Long studyId;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

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

    @OneToMany(mappedBy = "studyWeekly", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private final List<StudyWeeklyAttachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "studyWeekly", cascade = CascadeType.PERSIST)
    private final List<StudyWeeklySubmit> submits = new ArrayList<>();

    private StudyWeekly(
            final Long studyId,
            final Long creatorId,
            final String title,
            final String content,
            final int week,
            final Period period,
            final boolean assignmentExists,
            final boolean autoAttendance,
            final List<UploadAttachment> attachments
    ) {
        this.studyId = studyId;
        this.creatorId = creatorId;
        this.title = title;
        this.content = content;
        this.week = week;
        this.period = period;
        this.assignmentExists = assignmentExists;
        this.autoAttendance = autoAttendance;
        applyAttachments(attachments);
    }

    public static StudyWeekly createWeekly(
            final Long studyId,
            final Long creatorId,
            final String title,
            final String content,
            final int week,
            final Period period,
            final List<UploadAttachment> attachments
    ) {
        return new StudyWeekly(
                studyId,
                creatorId,
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
            final Long studyId,
            final Long creatorId,
            final String title,
            final String content,
            final int week,
            final Period period,
            final boolean autoAttendance,
            final List<UploadAttachment> attachments
    ) {
        return new StudyWeekly(
                studyId,
                creatorId,
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

    public void submitAssignment(final Long participantId, final UploadAssignment uploadAssignment) {
        submits.add(StudyWeeklySubmit.submitAssignment(this, participantId, uploadAssignment));
    }
}
