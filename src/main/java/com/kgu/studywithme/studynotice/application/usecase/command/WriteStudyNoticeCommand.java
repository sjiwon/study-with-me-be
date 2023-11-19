package com.kgu.studywithme.studynotice.application.usecase.command;

public record WriteStudyNoticeCommand(
        Long hostId,
        Long studyId,
        String title,
        String content
) {
}
