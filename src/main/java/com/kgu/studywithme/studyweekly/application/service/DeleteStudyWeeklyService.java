package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.application.usecase.command.DeleteStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class DeleteStudyWeeklyService implements DeleteStudyWeeklyUseCase {
    private final StudyWeeklyRepository studyWeeklyRepository;

    @Override
    public void deleteStudyWeekly(final Command command) {
        validateSpecificWeekIsLatestWeek(command.studyId(), command.weeklyId());
        studyWeeklyRepository.deleteSpecificWeekly(command.studyId(), command.weeklyId());
    }

    private void validateSpecificWeekIsLatestWeek(
            final Long studyId,
            final Long weeklyId
    ) {
        if (!studyWeeklyRepository.isLatestWeek(studyId, weeklyId)) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.ONLY_LATEST_WEEKLY_CAN_DELETE);
        }
    }
}
