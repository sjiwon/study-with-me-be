package com.kgu.studywithme.studynotice.application.usecase.command;

public interface UpdateStudyNoticeUseCase {
    void updateNotice(final Command command);

    record Command(
            Long hostId,
            Long noticeId,
            String title,
            String content
    ) {
    }
}
