package com.kgu.studywithme.studyreview.domain.model;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import jakarta.persistence.Column;
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
@Table(name = "study_review")
public class StudyReview extends BaseEntity<StudyReview> {
    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", referencedColumnName = "id", nullable = false)
    private Member writer;

    private StudyReview(final Study study, final Member writer, final String content) {
        this.study = study;
        this.writer = writer;
        this.content = content;
    }

    public static StudyReview writeReview(final Study study, final Member writer, final String content) {
        return new StudyReview(study, writer, content);
    }

    public void updateReview(final String content) {
        this.content = content;
    }

    public boolean isWriter(final Member other) {
        return writer.isSameMember(other);
    }
}
