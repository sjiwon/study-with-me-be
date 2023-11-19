package com.kgu.studywithme.studyweekly.application.usecase;

import com.kgu.studywithme.studyweekly.application.usecase.command.UpdateStudyWeeklyCommand;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;
import com.kgu.studywithme.studyweekly.domain.service.AttachmentUploader;
import com.kgu.studywithme.studyweekly.domain.service.WeeklyUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateStudyWeeklyUseCase {
    private final AttachmentUploader attachmentUploader;
    private final WeeklyUpdater weeklyUpdater;

    public void invoke(final UpdateStudyWeeklyCommand command) {
        final List<UploadAttachment> attachments = attachmentUploader.uploadAttachments(command.attachments());
        weeklyUpdater.invoke(
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
