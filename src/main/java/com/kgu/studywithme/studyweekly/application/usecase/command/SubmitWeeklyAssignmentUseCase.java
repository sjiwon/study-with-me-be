package com.kgu.studywithme.studyweekly.application.usecase.command;

import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;

public interface SubmitWeeklyAssignmentUseCase {
    void invoke(final Command command);

    record Command(
            Long memberId,
            Long studyId,
            Long weeklyId,
            UploadAssignment assignment
    ) {
    }
}
