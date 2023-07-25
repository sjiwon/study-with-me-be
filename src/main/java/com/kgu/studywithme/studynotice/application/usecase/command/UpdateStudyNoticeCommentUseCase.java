package com.kgu.studywithme.studynotice.application.usecase.command;

public interface UpdateStudyNoticeCommentUseCase {
    void updateNoticeComment(final Command command);

    record Command(
            Long commentId,
            Long memberId,
            String content
    ) {
    }
}
