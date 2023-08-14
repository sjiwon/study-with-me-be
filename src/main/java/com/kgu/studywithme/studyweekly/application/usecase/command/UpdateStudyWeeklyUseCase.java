package com.kgu.studywithme.studyweekly.application.usecase.command;

import com.kgu.studywithme.file.domain.RawFileData;
import com.kgu.studywithme.studyweekly.domain.Period;

import java.util.List;

public interface UpdateStudyWeeklyUseCase {
    void updateStudyWeekly(final Command command);

    record Command(
            Long weeklyId,
            String title,
            String content,
            Period period,
            boolean assignmentExists,
            boolean autoAttendance,
            List<RawFileData> files
    ) {
    }
}
