package com.kgu.studywithme.studynotice.application.usecase.command;

public interface DeleteStudyNoticeCommentUseCase {
    void invoke(final Command command);

    record Command(
            Long commentId,
            Long memberId
    ) {
    }
}
