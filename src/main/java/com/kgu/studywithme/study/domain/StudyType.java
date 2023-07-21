package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum StudyType {
    ONLINE("온라인", "online"),
    OFFLINE("오프라인", "offline");

    private final String description;
    private final String value;

    public static StudyType from(final String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_TYPE_IS_WEIRD));
    }
}
