package com.kgu.studywithme.studyweekly.application.usecase;

import com.kgu.studywithme.studyweekly.application.usecase.command.CreateStudyWeeklyCommand;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.service.AttachmentUploader;
import com.kgu.studywithme.studyweekly.domain.service.WeeklyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateStudyWeeklyUseCase {
    private final AttachmentUploader attachmentUploader;
    private final StudyWeeklyRepository studyWeeklyRepository;
    private final WeeklyManager weeklyManager;

    public Long invoke(final CreateStudyWeeklyCommand command) {
        final List<UploadAttachment> attachments = attachmentUploader.uploadAttachments(command.attachments());
        final int nextWeek = studyWeeklyRepository.getNextWeek(command.studyId());

        return weeklyManager.saveWeekly(createWeekly(command, attachments, nextWeek)).getId();
    }

    private StudyWeekly createWeekly(
            final CreateStudyWeeklyCommand command,
            final List<UploadAttachment> attachments,
            final int nextWeek
    ) {
        if (command.assignmentExists()) {
            return StudyWeekly.createWeeklyWithAssignment(
                    command.studyId(),
                    command.creatorId(),
                    command.title(),
                    command.content(),
                    nextWeek,
                    command.period(),
                    command.autoAttendance(),
                    attachments
            );
        }

        return StudyWeekly.createWeekly(
                command.studyId(),
                command.creatorId(),
                command.title(),
                command.content(),
                nextWeek,
                command.period(),
                attachments
        );
    }
}
