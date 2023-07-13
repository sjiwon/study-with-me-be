package com.kgu.studywithme.study.domain.review;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_review")
public class Review extends BaseEntity<Review> {
    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", referencedColumnName = "id", nullable = false)
    private Member writer;

    private Review(
            final Study study,
            final Member writer,
            final String content
    ) {
        this.study = study;
        this.writer = writer;
        this.content = content;
    }

    public static Review writeReview(
            final Study study,
            final Member writer,
            final String content
    ) {
        return new Review(study, writer, content);
    }

    public boolean isSameMember(final Member other) {
        return this.writer.isSameMember(other);
    }

    public void updateReview(final String content) {
        this.content = content;
    }
}
