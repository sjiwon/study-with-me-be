package com.kgu.studywithme.study.domain.week.submit;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.kgu.studywithme.study.domain.week.submit.UploadType.FILE;
import static com.kgu.studywithme.study.domain.week.submit.UploadType.LINK;

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

    private UploadAssignment(String uploadFileName, String link, UploadType type) {
        this.uploadFileName = uploadFileName;
        this.link = link;
        this.type = type;
    }

    public static UploadAssignment withLink(String link) {
        return new UploadAssignment(null, link, LINK);
    }

    public static UploadAssignment withFile(String uploadFileName, String link) {
        return new UploadAssignment(uploadFileName, link, FILE);
    }
}
