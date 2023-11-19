package com.kgu.studywithme.studynotice.domain.model;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", referencedColumnName = "id", nullable = false)
    private Member writer;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.PERSIST)
    private List<StudyNoticeComment> comments = new ArrayList<>();

    private StudyNotice(final Study study, final Member writer, final String title, final String content) {
        this.study = study;
        this.writer = writer;
        this.title = title;
        this.content = content;
    }

    public static StudyNotice writeNotice(final Study study, final Member writer, final String title, final String content) {
        return new StudyNotice(study, writer, title, content);
    }

    public void updateNoticeInformation(final String title, final String content) {
        this.title = title;
        this.content = content;
    }

    public void addComment(final Member writer, final String content) {
        comments.add(StudyNoticeComment.writeComment(this, writer, content));
    }
}
