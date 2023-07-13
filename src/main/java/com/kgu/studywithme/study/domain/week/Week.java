package com.kgu.studywithme.study.domain.week;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.week.attachment.Attachment;
import com.kgu.studywithme.study.domain.week.attachment.UploadAttachment;
import com.kgu.studywithme.study.domain.week.submit.Submit;
import com.kgu.studywithme.study.domain.week.submit.UploadAssignment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_week")
public class Week extends BaseEntity<Week> {
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

    @OneToMany(mappedBy = "week", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "week", cascade = CascadeType.PERSIST)
    private List<Submit> submits = new ArrayList<>();

    private Week(
            final Study study,
            final String title,
            final String content,
            final int week,
            final Period period,
            final boolean assignmentExists,
            final boolean autoAttendance,
            final List<UploadAttachment> attachments
    ) {
        this.study = study;
        this.creator = study.getHost();
        this.title = title;
        this.content = content;
        this.week = week;
        this.period = period;
        this.assignmentExists = assignmentExists;
        this.autoAttendance = autoAttendance;
        applyAttachments(attachments);
    }

    public static Week createWeek(
            final Study study,
            final String title,
            final String content,
            final int week,
            final Period period,
            final List<UploadAttachment> attachments
    ) {
        return new Week(study, title, content, week, period, false, false, attachments);
    }

    public static Week createWeekWithAssignment(
            final Study study,
            final String title,
            final String content,
            final int week,
            final Period period,
            final boolean autoAttendance,
            final List<UploadAttachment> attachments
    ) {
        return new Week(study, title, content, week, period, true, autoAttendance, attachments);
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
                            .map(uploadAttachment -> Attachment.addAttachmentFile(this, uploadAttachment))
                            .toList()
            );
        }
    }

    public void submitAssignment(
            final Member participant,
            final UploadAssignment uploadAssignment
    ) {
        submits.add(Submit.submitAssignment(this, participant, uploadAssignment));
    }
}
