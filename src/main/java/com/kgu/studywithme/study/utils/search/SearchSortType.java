package com.kgu.studywithme.study.utils.search;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SearchSortType {
    DATE("date"),
    FAVORITE("favorite"),
    REVIEW("review"),
    ;

    private final String value;

    public static SearchSortType from(final String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.INVALID_SORT_TYPE));
    }
}
