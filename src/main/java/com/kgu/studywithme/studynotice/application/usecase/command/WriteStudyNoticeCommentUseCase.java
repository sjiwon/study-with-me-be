package com.kgu.studywithme.studynotice.application.usecase.command;

public interface WriteStudyNoticeCommentUseCase {
    void invoke(final Command command);

    record Command(
            Long noticeId,
            Long writerId,
            String content
    ) {
    }
}
