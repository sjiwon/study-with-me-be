package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.studyweekly.application.adapter.StudyWeeklyHandlingRepositoryAdapter;
import com.kgu.studywithme.studyweekly.application.usecase.command.CreateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.event.WeeklyCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateStudyWeeklyService implements CreateStudyWeeklyUseCase {
    private final StudyWeeklyHandlingRepositoryAdapter studyWeeklyHandlingRepositoryAdapter;
    private final StudyWeeklyRepository studyWeeklyRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Long invoke(final Command command) {
        final int nextWeek = studyWeeklyHandlingRepositoryAdapter.getNextWeek(command.studyId());
        final StudyWeekly weekly = studyWeeklyRepository.save(createWeekly(command, nextWeek));

        eventPublisher.publishEvent(new WeeklyCreatedEvent(command.studyId(), weekly.getWeek()));
        return weekly.getId();
    }

    private StudyWeekly createWeekly(final Command command, final int nextWeek) {
        if (command.assignmentExists()) {
            return StudyWeekly.createWeeklyWithAssignment(
                    command.studyId(),
                    command.creatorId(),
                    command.title(),
                    command.content(),
                    nextWeek,
                    command.period(),
                    command.autoAttendance(),
                    command.attachments()
            );
        }

        return StudyWeekly.createWeekly(
                command.studyId(),
                command.creatorId(),
                command.title(),
                command.content(),
                nextWeek,
                command.period(),
                command.attachments()
        );
    }
}
