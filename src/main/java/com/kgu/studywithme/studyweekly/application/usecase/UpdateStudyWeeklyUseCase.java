package com.kgu.studywithme.studyweekly.application.usecase;

import com.kgu.studywithme.studyweekly.application.usecase.command.UpdateStudyWeeklyCommand;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;
import com.kgu.studywithme.studyweekly.domain.service.AttachmentUploader;
import com.kgu.studywithme.studyweekly.domain.service.WeeklyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateStudyWeeklyUseCase {
    private final AttachmentUploader attachmentUploader;
    private final WeeklyManager weeklyManager;

    public void invoke(final UpdateStudyWeeklyCommand command) {
        final List<UploadAttachment> attachments = attachmentUploader.uploadAttachments(command.attachments());
        weeklyManager.updateWeekly(
                command.weeklyId(),
                command.title(),
                command.content(),
                command.period(),
                command.assignmentExists(),
                command.autoAttendance(),
                attachments
        );
    }
}
