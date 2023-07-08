package com.kgu.studywithme.study.domain.week.attachment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class UploadAttachment {
    @Column(name = "upload_file_name", nullable = false)
    private String uploadFileName;

    @Column(name = "link", nullable = false)
    private String link;

    private UploadAttachment(
            final String uploadFileName,
            final String link
    ) {
        this.uploadFileName = uploadFileName;
        this.link = link;
    }

    public static UploadAttachment of(
            final String uploadFileName,
            final String link
    ) {
        return new UploadAttachment(uploadFileName, link);
    }
}
