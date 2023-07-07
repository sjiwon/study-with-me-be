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

    private UploadAttachment(String uploadFileName, String link) {
        this.uploadFileName = uploadFileName;
        this.link = link;
    }

    public static UploadAttachment of(String uploadFileName, String link) {
        return new UploadAttachment(uploadFileName, link);
    }
}
