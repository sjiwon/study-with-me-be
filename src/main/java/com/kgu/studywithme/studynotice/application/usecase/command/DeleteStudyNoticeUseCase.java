package com.kgu.studywithme.studynotice.application.usecase.command;

public interface DeleteStudyNoticeUseCase {
    void invoke(final Command command);

    record Command(
            Long hostId,
            Long noticeId
    ) {
    }
}
