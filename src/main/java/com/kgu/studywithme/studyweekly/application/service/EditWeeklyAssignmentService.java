package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.application.adapter.StudyWeeklyHandlingRepositoryAdapter;
import com.kgu.studywithme.studyweekly.application.usecase.command.EditWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.domain.submit.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.event.AssignmentEditedEvent;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class EditWeeklyAssignmentService implements EditWeeklyAssignmentUseCase {
    private final StudyWeeklyHandlingRepositoryAdapter studyWeeklyHandlingRepositoryAdapter;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void invoke(final Command command) {
        final StudyWeeklySubmit submittedAssignment
                = getSubmittedAssignment(command.memberId(), command.studyId(), command.weeklyId());
        submittedAssignment.editUpload(command.assignment());

        eventPublisher.publishEvent(new AssignmentEditedEvent(command.studyId(), command.weeklyId(), command.memberId()));
    }

    private StudyWeeklySubmit getSubmittedAssignment(final Long memberId, final Long studyId, final Long weeklyId) {
        return studyWeeklyHandlingRepositoryAdapter.getSubmittedAssignment(memberId, studyId, weeklyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyWeeklyErrorCode.SUBMITTED_ASSIGNMENT_NOT_FOUND));
    }
}
