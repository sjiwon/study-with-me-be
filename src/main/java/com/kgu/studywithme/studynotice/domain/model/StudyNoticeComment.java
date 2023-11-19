package com.kgu.studywithme.studynotice.domain.model;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_notice_comment")
public class StudyNoticeComment extends BaseEntity<StudyNoticeComment> {
    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notice_id", referencedColumnName = "id", nullable = false)
    private StudyNotice notice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", referencedColumnName = "id", nullable = false)
    private Member writer;

    private StudyNoticeComment(final StudyNotice notice, final Member writer, final String content) {
        this.notice = notice;
        this.writer = writer;
        this.content = content;
    }

    public static StudyNoticeComment writeComment(final StudyNotice notice, final Member writer, final String content) {
        return new StudyNoticeComment(notice, writer, content);
    }

    public void updateComment(final String content) {
        this.content = content;
    }

    public boolean isWriter(final Member other) {
        return writer.isSameMember(other);
    }
}
