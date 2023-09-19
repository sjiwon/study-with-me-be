package com.kgu.studywithme.studyweekly.domain.model;

import com.kgu.studywithme.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_weekly_submit")
public class StudyWeeklySubmit extends BaseEntity<StudyWeeklySubmit> {
    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    @Embedded
    private UploadAssignment uploadAssignment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_id", referencedColumnName = "id", nullable = false)
    private StudyWeekly weekly;

    private StudyWeeklySubmit(
            final StudyWeekly weekly,
            final Long participantId,
            final UploadAssignment uploadAssignment
    ) {
        this.weekly = weekly;
        this.participantId = participantId;
        this.uploadAssignment = uploadAssignment;
    }

    public static StudyWeeklySubmit submitAssignment(
            final StudyWeekly weekly,
            final Long participantId,
            final UploadAssignment uploadAssignment
    ) {
        return new StudyWeeklySubmit(weekly, participantId, uploadAssignment);
    }

    public void editUpload(final UploadAssignment uploadAssignment) {
        this.uploadAssignment = uploadAssignment;
    }
}
