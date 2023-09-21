package com.kgu.studywithme.studynotice.application.usecase.command;

public record UpdateStudyNoticeCommentCommand(
        Long commentId,
        Long memberId,
        String content
) {
}
