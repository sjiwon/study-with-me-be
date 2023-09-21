package com.kgu.studywithme.studyweekly.application.usecase.command;

public record DeleteStudyWeeklyCommand(
        Long studyId,
        Long weeklyId
) {
}
