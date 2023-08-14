package com.kgu.studywithme.studyweekly.application.usecase.command;

public interface DeleteStudyWeeklyUseCase {
    void invoke(final Command command);

    record Command(
            Long studyId,
            Long weeklyId
    ) {
    }
}
