package com.kgu.studywithme.studynotice.domain;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.studynotice.domain.comment.StudyNoticeComment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_notice")
public class StudyNotice extends BaseEntity<StudyNotice> {
    @Column(name = "study_id", nullable = false)
    private Long studyId;

    @Column(name = "writer_id", nullable = false)
    private Long writerId;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.PERSIST)
    private List<StudyNoticeComment> comments = new ArrayList<>();

    private StudyNotice(
            final Long studyId,
            final Long writerId,
            final String title,
            final String content
    ) {
        this.studyId = studyId;
        this.writerId = writerId;
        this.title = title;
        this.content = content;
    }

    public static StudyNotice writeNotice(
            final Long studyId,
            final Long writerId,
            final String title,
            final String content
    ) {
        return new StudyNotice(studyId, writerId, title, content);
    }

    public void updateNoticeInformation(
            final String title,
            final String content
    ) {
        this.title = title;
        this.content = content;
    }

    public void addComment(
            final Long writerId,
            final String content
    ) {
        comments.add(StudyNoticeComment.writeComment(this, writerId, content));
    }

    public boolean isWriter(final Long memberId) {
        return this.writerId.equals(memberId);
    }
}
