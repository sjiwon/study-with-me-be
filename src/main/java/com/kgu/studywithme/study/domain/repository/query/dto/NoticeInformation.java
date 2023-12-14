package com.kgu.studywithme.study.domain.repository.query.dto;

import com.kgu.studywithme.member.domain.model.Nickname;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class NoticeInformation {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private StudyMember writer;
    private List<CommentInformation> comments;

    public record CommentInformation(
            Long id,
            Long noticeId,
            String content,
            LocalDateTime writtenDate,
            StudyMember writer
    ) {
        @QueryProjection
        public CommentInformation(
                final Long id,
                final Long noticeId,
                final String content,
                final LocalDateTime writtenDate,
                final Long writerId,
                final Nickname writerNickname
        ) {
            this(
                    id,
                    noticeId,
                    content,
                    writtenDate,
                    new StudyMember(writerId, writerNickname.getValue())
            );
        }
    }

    @QueryProjection
    public NoticeInformation(
            final Long id,
            final String title,
            final String content,
            final LocalDateTime createdAt,
            final LocalDateTime lastModifiedAt,
            final Long writerId,
            final Nickname writerNickname
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.writer = new StudyMember(writerId, writerNickname.getValue());
    }

    public void applyComments(final List<CommentInformation> comments) {
        this.comments = comments;
    }
}
