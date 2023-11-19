package com.kgu.studywithme.studyweekly.domain.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studyweekly.domain.model.Period;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WeeklyUpdater {
    private final StudyWeeklyRepository studyWeeklyRepository;

    @StudyWithMeWritableTransactional
    public void invoke(
            final Long weeklyId,
            final String title,
            final String content,
            final Period period,
            final boolean assignmentExists,
            final boolean autoAttendance,
            final List<UploadAttachment> attachments
    ) {
        final StudyWeekly weekly = studyWeeklyRepository.getById(weeklyId);
        weekly.update(
                title,
                content,
                period,
                assignmentExists,
                autoAttendance,
                attachments
        );
    }
}
