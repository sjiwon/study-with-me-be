package com.kgu.studywithme.studyweekly.application.usecase.command;

import org.springframework.web.multipart.MultipartFile;

public interface EditSubmittedWeeklyAssignmentUseCase {
    void editSubmittedWeeklyAssignment(final Command command);

    record Command(
            Long memberId,
            Long studyId,
            int week,
            String uploadType,
            MultipartFile file,
            String link
    ) {
    }
}
