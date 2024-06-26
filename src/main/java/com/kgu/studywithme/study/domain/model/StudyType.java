package com.kgu.studywithme.study.domain.model;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum StudyType {
    ONLINE("online"),
    OFFLINE("offline");

    private final String value;

    public static StudyType from(final String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_TYPE_IS_WEIRD));
    }
}
