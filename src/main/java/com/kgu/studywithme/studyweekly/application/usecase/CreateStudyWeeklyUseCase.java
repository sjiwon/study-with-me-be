package com.kgu.studywithme.studyweekly.application.usecase;

import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.CreateStudyWeeklyCommand;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.service.AttachmentUploader;
import com.kgu.studywithme.studyweekly.domain.service.WeeklyCreator;
import lombok.RequiredArgsConstructor;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class CreateStudyWeeklyUseCase {
    private final AttachmentUploader attachmentUploader;
    private final StudyWeeklyRepository studyWeeklyRepository;
    private final WeeklyCreator weeklyCreator;

    public Long invoke(final CreateStudyWeeklyCommand command) {
        final List<UploadAttachment> attachments = attachmentUploader.uploadAttachments(command.attachments());
        final int nextWeek = studyWeeklyRepository.getNextWeek(command.studyId());

        return weeklyCreator.invoke(command, attachments, nextWeek).getId();
    }
}
