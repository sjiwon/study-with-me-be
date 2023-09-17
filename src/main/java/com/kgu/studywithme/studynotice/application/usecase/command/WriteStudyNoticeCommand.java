package com.kgu.studywithme.studynotice.application.usecase.command;

import com.kgu.studywithme.studynotice.domain.model.StudyNotice;

public record WriteStudyNoticeCommand(
        Long hostId,
        Long studyId,
        String title,
        String content
) {
    public StudyNotice toDomain() {
        return StudyNotice.writeNotice(studyId, hostId, title, content);
    }
}
