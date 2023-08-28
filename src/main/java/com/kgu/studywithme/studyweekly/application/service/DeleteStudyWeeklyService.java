package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.application.adapter.StudyWeeklyHandlingRepositoryAdapter;
import com.kgu.studywithme.studyweekly.application.usecase.command.DeleteStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteStudyWeeklyService implements DeleteStudyWeeklyUseCase {
    private final StudyWeeklyHandlingRepositoryAdapter studyWeeklyHandlingRepositoryAdapter;

    @Override
    public void invoke(final Command command) {
        validateSpecificWeekIsLatestWeek(command.studyId(), command.weeklyId());
        studyWeeklyHandlingRepositoryAdapter.deleteSpecificWeekly(command.studyId(), command.weeklyId());
    }

    private void validateSpecificWeekIsLatestWeek(
            final Long studyId,
            final Long weeklyId
    ) {
        if (!studyWeeklyHandlingRepositoryAdapter.isLatestWeek(studyId, weeklyId)) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.ONLY_LATEST_WEEKLY_CAN_DELETE);
        }
    }
}
