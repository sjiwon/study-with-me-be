package com.kgu.studywithme.studyweekly.application.usecase.command;

public interface DeleteStudyWeeklyUseCase {
    void deleteStudyWeekly(final Command command);

    record Command(
            Long studyId,
            int week
    ) {
    }
}
