package com.kgu.studywithme.studyweekly.domain.service;

import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType;
import com.kgu.studywithme.studyweekly.domain.model.UploadAssignment;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.FILE;
import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.LINK;

@Service
@RequiredArgsConstructor
public class AssignmentUploader {
    private final FileUploader fileUploader;

    public UploadAssignment uploadAssignment(
            final AssignmentSubmitType submitType,
            final RawFileData file,
            final String link
    ) {
        validateAssignmentSubmissionExists(submitType, file, link);

        if (submitType == FILE) {
            return UploadAssignment.withFile(file.fileName(), fileUploader.uploadFile(file));
        }
        return UploadAssignment.withLink(link);
    }

    private void validateAssignmentSubmissionExists(
            final AssignmentSubmitType submitType,
            final RawFileData file,
            final String link
    ) {
        if (file == null && link == null) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.MISSING_SUBMISSION);
        }

        if (file != null && link != null) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.DUPLICATE_SUBMISSION);
        }

        if (submitType == FILE && (file == null)) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.INVALID_BETWEEN_SUBMIT_TYPE_AND_RESULT);
        }

        if (submitType == LINK && link == null) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.INVALID_BETWEEN_SUBMIT_TYPE_AND_RESULT);
        }
    }
}
