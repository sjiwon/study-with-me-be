package com.kgu.studywithme.studyweekly.domain.model;

import com.kgu.studywithme.global.BaseEntity;
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
@Table(name = "study_weekly_attachment")
public class StudyWeeklyAttachment extends BaseEntity<StudyWeeklyAttachment> {
    @Embedded
    private UploadAttachment uploadAttachment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_id", referencedColumnName = "id", nullable = false)
    private StudyWeekly studyWeekly;

    private StudyWeeklyAttachment(final StudyWeekly studyWeekly, final UploadAttachment uploadAttachment) {
        this.studyWeekly = studyWeekly;
        this.uploadAttachment = uploadAttachment;
    }

    public static StudyWeeklyAttachment addAttachmentFile(
            final StudyWeekly studyWeekly,
            final UploadAttachment uploadAttachment
    ) {
        return new StudyWeeklyAttachment(studyWeekly, uploadAttachment);
    }
}
