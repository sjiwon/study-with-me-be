package com.kgu.studywithme.study.infrastructure.query.dto;

import com.kgu.studywithme.member.domain.model.Nickname;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class NoticeInformation {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastModifiedAt;
    private final StudyMember writer;
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
