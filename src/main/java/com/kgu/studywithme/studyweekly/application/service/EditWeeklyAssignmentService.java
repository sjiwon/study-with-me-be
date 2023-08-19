package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.RawFileData;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.application.adapter.StudyWeeklyHandlingRepositoryAdapter;
import com.kgu.studywithme.studyweekly.application.usecase.command.EditWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType;
import com.kgu.studywithme.studyweekly.domain.submit.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;
import com.kgu.studywithme.studyweekly.event.AssignmentEditedEvent;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType.FILE;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class EditWeeklyAssignmentService implements EditWeeklyAssignmentUseCase {
    private final StudyWeeklyHandlingRepositoryAdapter studyWeeklyHandlingRepositoryAdapter;
    private final FileUploader uploader;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void invoke(final Command command) {
        validateAssignmentSubmissionExists(command.file(), command.link());

        final StudyWeeklySubmit submittedAssignment = getSubmittedAssignment(command.memberId(), command.studyId(), command.weeklyId());
        final UploadAssignment assignment = uploadAssignment(command.submitType(), command.file(), command.link());
        submittedAssignment.editUpload(assignment);

        eventPublisher.publishEvent(new AssignmentEditedEvent(command.studyId(), command.weeklyId(), command.memberId()));
    }

    private void validateAssignmentSubmissionExists(
            final RawFileData file,
            final String link
    ) {
        if (file == null && link == null) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.MISSING_SUBMISSION);
        }

        if (file != null && link != null) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.DUPLICATE_SUBMISSION);
        }
    }

    private StudyWeeklySubmit getSubmittedAssignment(
            final Long memberId,
            final Long studyId,
            final Long weeklyId
    ) {
        return studyWeeklyHandlingRepositoryAdapter.getSubmittedAssignment(memberId, studyId, weeklyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyWeeklyErrorCode.SUBMITTED_ASSIGNMENT_NOT_FOUND));
    }

    private UploadAssignment uploadAssignment(
            final AssignmentSubmitType submitType,
            final RawFileData file,
            final String link
    ) {
        return submitType == FILE
                ? UploadAssignment.withFile(file.originalFileName(), uploader.uploadWeeklySubmit(file))
                : UploadAssignment.withLink(link);
    }
}
