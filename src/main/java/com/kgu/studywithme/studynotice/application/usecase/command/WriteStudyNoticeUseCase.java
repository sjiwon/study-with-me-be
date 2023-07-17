package com.kgu.studywithme.studynotice.application.usecase.command;

public interface WriteStudyNoticeUseCase {
    Long writeNotice(final Command command);

    record Command(
            Long hostId,
            Long studyId,
            String title,
            String content
    ) {
    }
}
