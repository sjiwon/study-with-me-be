package com.kgu.studywithme.study.application.usecase.command;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.study.domain.model.Capacity;
import com.kgu.studywithme.study.domain.model.Description;
import com.kgu.studywithme.study.domain.model.StudyName;
import com.kgu.studywithme.study.domain.model.StudyThumbnail;
import com.kgu.studywithme.study.domain.model.StudyType;

import java.util.Set;

public record CreateStudyCommand(
        Long hostId,
        StudyName name,
        Description description,
        Category category,
        Capacity capacity,
        StudyThumbnail thumbnail,
        StudyType type,
        String province,
        String city,
        int minimumAttendanceForGraduation,
        Set<String> hashtags
) {
}
