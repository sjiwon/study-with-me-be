package com.kgu.studywithme.studyreview.domain;

import com.kgu.studywithme.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "study_review",
        indexes = {
                @Index(name = "idx_study_review_writer_id", columnList = "writer_id"),
                @Index(name = "idx_study_review_study_id", columnList = "study_id")
        }
)
public class StudyReview extends BaseEntity<StudyReview> {
    @Column(name = "study_id", nullable = false)
    private Long studyId;

    @Column(name = "writer_id", nullable = false)
    private Long writerId;

    @Column(name = "content", nullable = false)
    private String content;

    private StudyReview(
            final Long studyId,
            final Long writerId,
            final String content
    ) {
        this.studyId = studyId;
        this.writerId = writerId;
        this.content = content;
    }

    public static StudyReview writeReview(
            final Long studyId,
            final Long writerId,
            final String content
    ) {
        return new StudyReview(studyId, writerId, content);
    }

    public void updateReview(final String content) {
        this.content = content;
    }

    public boolean isWriter(final Long memberId) {
        return this.writerId.equals(memberId);
    }
}
