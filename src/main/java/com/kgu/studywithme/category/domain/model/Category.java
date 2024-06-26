package com.kgu.studywithme.category.domain.model;

import com.kgu.studywithme.category.exception.CategoryErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Category {
    LANGUAGE(1L, "어학"),
    INTERVIEW(2L, "면접"),
    PROGRAMMING(3L, "프로그래밍"),
    APTITUDE_NCS(4L, "인적성 & NCS"),
    CERTIFICATION(5L, "자격증"),
    ETC(6L, "기타"),
    ;

    private final long id;
    private final String name;

    public static Category from(final long id) {
        return Arrays.stream(values())
                .filter(category -> category.id == id)
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(CategoryErrorCode.CATEGORY_NOT_EXIST));
    }

    public static Set<Category> of(final Set<Long> ids) {
        return ids.stream()
                .map(Category::from)
                .collect(Collectors.toSet());
    }
}
