package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.application.usecase.command.UpdateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import com.kgu.studywithme.studyweekly.infrastructure.persistence.StudyWeeklyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateStudyWeeklyService implements UpdateStudyWeeklyUseCase {
    private final StudyWeeklyJpaRepository studyWeeklyJpaRepository;

    @Override
    public void invoke(final Command command) {
        final StudyWeekly weekly = getSpecificWeekly(command.weeklyId());

        weekly.update(
                command.title(),
                command.content(),
                command.period(),
                command.assignmentExists(),
                command.autoAttendance(),
                command.attachments()
        );
    }

    private StudyWeekly getSpecificWeekly(final Long weeklyId) {
        return studyWeeklyJpaRepository.findById(weeklyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND));
    }
}
