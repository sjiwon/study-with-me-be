package com.kgu.studywithme.studyweekly.domain.submit;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.kgu.studywithme.studyweekly.domain.submit.UploadType.FILE;
import static com.kgu.studywithme.studyweekly.domain.submit.UploadType.LINK;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class UploadAssignment {
    @Column(name = "upload_file_name")
    private String uploadFileName;

    @Column(name = "link", nullable = false)
    private String link;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_type", nullable = false)
    private UploadType type;

    private UploadAssignment(
            final String uploadFileName,
            final String link,
            final UploadType type
    ) {
        this.uploadFileName = uploadFileName;
        this.link = link;
        this.type = type;
    }

    public static UploadAssignment withLink(final String link) {
        return new UploadAssignment(null, link, LINK);
    }

    public static UploadAssignment withFile(
            final String uploadFileName,
            final String link
    ) {
        return new UploadAssignment(uploadFileName, link, FILE);
    }
}