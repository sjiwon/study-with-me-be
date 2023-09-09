package com.kgu.studywithme.studyweekly.domain.submit;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType.FILE;
import static com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType.LINK;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class UploadAssignment {
    @Column(name = "upload_file_name")
    private String uploadFileName;

    @Column(name = "link", nullable = false)
    private String link;

    @Enumerated(EnumType.STRING)
    @Column(name = "submit_type", nullable = false)
    private AssignmentSubmitType submitType;

    private UploadAssignment(final String uploadFileName, final String link, final AssignmentSubmitType submitType) {
        this.uploadFileName = uploadFileName;
        this.link = link;
        this.submitType = submitType;
    }

    public static UploadAssignment withLink(final String link) {
        return new UploadAssignment(null, link, LINK);
    }

    public static UploadAssignment withFile(final String uploadFileName, final String link) {
        return new UploadAssignment(uploadFileName, link, FILE);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UploadAssignment other = (UploadAssignment) o;

        if (!Objects.equals(uploadFileName, other.uploadFileName)) return false;
        if (!link.equals(other.link)) return false;
        return submitType == other.submitType;
    }

    @Override
    public int hashCode() {
        int result = uploadFileName != null ? uploadFileName.hashCode() : 0;
        result = 31 * result + link.hashCode();
        result = 31 * result + submitType.hashCode();
        return result;
    }
}
