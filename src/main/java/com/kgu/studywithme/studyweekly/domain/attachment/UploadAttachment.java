package com.kgu.studywithme.studyweekly.domain.attachment;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UploadAttachment other = (UploadAttachment) o;

        if (!uploadFileName.equals(other.uploadFileName)) return false;
        return link.equals(other.link);
    }

    @Override
    public int hashCode() {
        int result = uploadFileName.hashCode();
        result = 31 * result + link.hashCode();
        return result;
    }
}
