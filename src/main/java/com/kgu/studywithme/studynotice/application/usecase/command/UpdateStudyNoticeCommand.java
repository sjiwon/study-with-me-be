package com.kgu.studywithme.studynotice.application.usecase.command;

public record UpdateStudyNoticeCommand(
        Long noticeId,
        String title,
        String content
) {
}
