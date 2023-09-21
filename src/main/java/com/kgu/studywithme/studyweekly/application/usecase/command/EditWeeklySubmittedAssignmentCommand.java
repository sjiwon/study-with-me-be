package com.kgu.studywithme.studyweekly.application.usecase.command;

import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType;

public record EditWeeklySubmittedAssignmentCommand(
        Long memberId,
        Long studyId,
        Long weeklyId,
        AssignmentSubmitType submitType,
        RawFileData assignment,
        String linkSubmit
) {
}
