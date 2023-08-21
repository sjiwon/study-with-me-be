package com.kgu.studywithme.studyweekly.application.usecase.command;

import com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;

public interface EditWeeklyAssignmentUseCase {
    void invoke(final Command command);

    record Command(
            Long memberId,
            Long studyId,
            Long weeklyId,
            AssignmentSubmitType submitType,
            UploadAssignment file,
            String link
    ) {
    }
}