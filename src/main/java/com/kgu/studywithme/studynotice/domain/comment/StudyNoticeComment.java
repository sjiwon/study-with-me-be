package com.kgu.studywithme.studynotice.domain.comment;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.studynotice.domain.StudyNotice;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_notice_comment")
public class StudyNoticeComment extends BaseEntity<StudyNoticeComment> {
    @Column(name = "writer_id", nullable = false)
    private Long writerId;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notice_id", referencedColumnName = "id", nullable = false)
    private StudyNotice notice;

    private StudyNoticeComment(
            final StudyNotice notice,
            final Long writerId,
            final String content
    ) {
        this.notice = notice;
        this.writerId = writerId;
        this.content = content;
    }

    public static StudyNoticeComment writeComment(
            final StudyNotice notice,
            final Long writerId,
            final String content
    ) {
        return new StudyNoticeComment(notice, writerId, content);
    }

    public void updateComment(final String content) {
        this.content = content;
    }

    public boolean isWriter(final Long memberId) {
        return this.writerId.equals(memberId);
    }
}
