package com.kgu.studywithme.studyweekly.application.usecase.command;

import com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType;
import org.springframework.web.multipart.MultipartFile;

public interface EditSubmittedWeeklyAssignmentUseCase {
    void editSubmittedWeeklyAssignment(final Command command);

    record Command(
            Long memberId,
            Long studyId,
            Long weeklyId,
            AssignmentSubmitType submitType,
            MultipartFile file,
            String link
    ) {
    }
}
