package com.kgu.studywithme.studyweekly.application.usecase.command;

import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.studyweekly.domain.model.Period;

import java.util.List;

public record UpdateStudyWeeklyCommand(
        Long weeklyId,
        String title,
        String content,
        Period period,
        boolean assignmentExists,
        boolean autoAttendance,
        List<RawFileData> attachments
) {
}
