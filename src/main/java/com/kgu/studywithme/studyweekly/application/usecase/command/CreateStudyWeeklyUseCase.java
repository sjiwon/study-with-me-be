package com.kgu.studywithme.studyweekly.application.usecase.command;

import com.kgu.studywithme.file.domain.RawFileData;
import com.kgu.studywithme.studyweekly.domain.Period;

import java.util.List;

public interface CreateStudyWeeklyUseCase {
    Long createStudyWeekly(final Command command);

    record Command(
            Long studyId,
            Long creatorId,
            String title,
            String content,
            Period period,
            boolean assignmentExists,
            boolean autoAttendance,
            List<RawFileData> files
    ) {
    }
}
