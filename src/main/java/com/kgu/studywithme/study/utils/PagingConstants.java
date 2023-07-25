package com.kgu.studywithme.study.utils;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

public interface PagingConstants {
    int SLICE_PER_PAGE = 8;

    @Getter
    @RequiredArgsConstructor
    enum SortType {
        DATE("date"),
        FAVORITE("favorite"),
        REVIEW("review"),
        ;

        private final String value;

        public static SortType from(final String value) {
            return Arrays.stream(values())
                    .filter(type -> type.value.equals(value))
                    .findFirst()
                    .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.INVALID_SORT_TYPE));
        }
    }

    static Pageable getDefaultPageRequest(final int page) {
        return PageRequest.of(page, SLICE_PER_PAGE);
    }
}
