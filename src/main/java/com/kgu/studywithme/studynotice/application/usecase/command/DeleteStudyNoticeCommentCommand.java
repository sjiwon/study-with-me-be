package com.kgu.studywithme.studynotice.application.usecase.command;

public record DeleteStudyNoticeCommentCommand(
        Long commentId,
        Long memberId
) {
}
