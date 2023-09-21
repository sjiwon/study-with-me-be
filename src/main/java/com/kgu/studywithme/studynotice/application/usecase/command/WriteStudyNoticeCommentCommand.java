package com.kgu.studywithme.studynotice.application.usecase.command;

public record WriteStudyNoticeCommentCommand(
        Long noticeId,
        Long writerId,
        String content
) {
}
