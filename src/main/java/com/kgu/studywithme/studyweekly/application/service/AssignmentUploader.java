package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType;
import com.kgu.studywithme.studyweekly.domain.model.UploadAssignment;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.FILE;
import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.LINK;

@Component
@RequiredArgsConstructor
public class AssignmentUploader {
    private final FileUploader fileUploader;

    public UploadAssignment uploadAssignmentWithFile(
            final AssignmentSubmitType submitType,
            final MultipartFile file,
            final String link
    ) {
        validateAssignmentSubmissionExists(submitType, file, link);

        if (submitType == FILE) {
            return UploadAssignment.withFile(file.getOriginalFilename(), fileUploader.uploadWeeklySubmit(file));
        }
        return UploadAssignment.withLink(link);
    }

    private void validateAssignmentSubmissionExists(
            final AssignmentSubmitType submitType,
            final MultipartFile file,
            final String link
    ) {
        if (file == null && link == null) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.MISSING_SUBMISSION);
        }

        if (file != null && link != null) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.DUPLICATE_SUBMISSION);
        }

        if (submitType == FILE && (file == null || file.isEmpty())) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.INVALID_BETWEEN_SUBMIT_TYPE_AND_RESULT);
        }

        if (submitType == LINK && link == null) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.INVALID_BETWEEN_SUBMIT_TYPE_AND_RESULT);
        }
    }
}
