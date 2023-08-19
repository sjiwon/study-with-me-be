package com.kgu.studywithme.studyweekly.application.usecase.command;

import com.kgu.studywithme.studyweekly.domain.Period;
import com.kgu.studywithme.studyweekly.domain.attachment.UploadAttachment;

import java.util.List;

public interface UpdateStudyWeeklyUseCase {
    void invoke(final Command command);

    record Command(
            Long weeklyId,
            String title,
            String content,
            Period period,
            boolean assignmentExists,
            boolean autoAttendance,
            List<UploadAttachment> attachments
    ) {
    }
}
