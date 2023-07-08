package com.kgu.studywithme.study.domain.week.attachment;

import com.kgu.studywithme.study.domain.week.Week;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_week_attachment")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private UploadAttachment uploadAttachment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_id", referencedColumnName = "id", nullable = false)
    private Week week;

    private Attachment(
            final Week week,
            final UploadAttachment uploadAttachment
    ) {
        this.week = week;
        this.uploadAttachment = uploadAttachment;
    }

    public static Attachment addAttachmentFile(
            final Week week,
            final UploadAttachment uploadAttachment
    ) {
        return new Attachment(week, uploadAttachment);
    }
}
