package com.kgu.studywithme.studyweekly.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.application.usecase.command.DeleteStudyWeeklyCommand;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.service.WeeklyManager;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteStudyWeeklyUseCase {
    private final StudyWeeklyRepository studyWeeklyRepository;
    private final WeeklyManager weeklyManager;

    @StudyWithMeWritableTransactional
    public void invoke(final DeleteStudyWeeklyCommand command) {
        validateDeleteTargetWeeklyIsLatestWeek(command.studyId(), command.weeklyId());

        final StudyWeekly weekly = studyWeeklyRepository.getById(command.weeklyId());
        weeklyManager.deleteWeekly(weekly);
    }

    private void validateDeleteTargetWeeklyIsLatestWeek(final Long studyId, final Long weeklyId) {
        if (!studyWeeklyRepository.isLatestWeek(studyId, weeklyId)) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.ONLY_LATEST_WEEKLY_CAN_DELETE);
        }
    }
}
