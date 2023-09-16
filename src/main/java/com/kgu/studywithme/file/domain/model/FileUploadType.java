package com.kgu.studywithme.file.domain.model;

import com.kgu.studywithme.file.exception.FileErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FileUploadType {
    STUDY_DESCRIPTION_IMAGE("studyDescriptionImage"),
    STUDY_WEEKLY_CONTENT_IMAGE("studyWeeklyContentImage"),
    STUDY_WEEKLY_ATTACHMENT("studyWeeklyAttachment"),
    STUDY_WEEKLY_ASSIGNMENT("studyWeeklyAssignment"),
    ;

    private final String value;

    public static FileUploadType from(final String value) {
        return Arrays.stream(values())
                .filter(uploadType -> uploadType.value.equals(value))
                .findAny()
                .orElseThrow(() -> StudyWithMeException.type(FileErrorCode.INVALID_UPLOAD_TYPE));
    }
}
