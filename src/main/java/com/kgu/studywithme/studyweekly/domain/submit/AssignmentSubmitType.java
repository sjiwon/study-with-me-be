package com.kgu.studywithme.studyweekly.domain.submit;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AssignmentSubmitType {
    LINK("link"),
    FILE("file"),
    ;

    private final String value;

    public static AssignmentSubmitType from(final String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(StudyWeeklyErrorCode.INVALID_SUBMIT_TYPE));
    }
}
