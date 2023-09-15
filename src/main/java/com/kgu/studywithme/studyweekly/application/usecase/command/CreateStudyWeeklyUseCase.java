package com.kgu.studywithme.studyweekly.application.usecase.command;

import com.kgu.studywithme.studyweekly.domain.model.Period;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;

import java.util.List;

public interface CreateStudyWeeklyUseCase {
    Long invoke(final Command command);

    record Command(
            Long studyId,
            Long creatorId,
            String title,
            String content,
            Period period,
            boolean assignmentExists,
            boolean autoAttendance,
            List<UploadAttachment> attachments
    ) {
    }
}
