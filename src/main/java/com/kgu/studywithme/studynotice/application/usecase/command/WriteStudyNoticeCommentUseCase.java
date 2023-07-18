package com.kgu.studywithme.studynotice.application.usecase.command;

public interface WriteStudyNoticeCommentUseCase {
    void writeNoticeComment(final Command command);

    record Command(
            Long noticeId,
            Long memberId,
            String content
    ) {
    }
}
