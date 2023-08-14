package com.kgu.studywithme.studyweekly.application.usecase.command;

import com.kgu.studywithme.file.domain.RawFileData;
import com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType;

public interface SubmitWeeklyAssignmentUseCase {
    void submitWeeklyAssignment(final Command command);

    record Command(
            Long memberId,
            Long studyId,
            Long weeklyId,
            AssignmentSubmitType submitType,
            RawFileData file,
            String link
    ) {
    }
}
