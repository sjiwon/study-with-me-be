package com.kgu.studywithme.studyweekly.application.usecase;

import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.EditWeeklySubmittedAssignmentCommand;
import com.kgu.studywithme.studyweekly.domain.model.UploadAssignment;
import com.kgu.studywithme.studyweekly.domain.service.AssignmentUploader;
import com.kgu.studywithme.studyweekly.domain.service.WeeklySubmitManager;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class EditWeeklySubmittedAssignmentUseCase {
    private final AssignmentUploader assignmentUploader;
    private final WeeklySubmitManager weeklySubmitManager;

    public void invoke(final EditWeeklySubmittedAssignmentCommand command) {
        final UploadAssignment assignment = assignmentUploader.uploadAssignment(command.submitType(), command.assignment(), command.linkSubmit());
        weeklySubmitManager.editSubmittedAssignment(command.memberId(), command.studyId(), command.weeklyId(), assignment);
    }
}
