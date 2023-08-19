package com.kgu.studywithme.study.application.usecase.command;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.domain.Capacity;
import com.kgu.studywithme.study.domain.Description;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.StudyThumbnail;
import com.kgu.studywithme.study.domain.StudyType;

import java.util.Set;

public interface CreateStudyUseCase {
    Long invoke(final Command command);

    record Command(
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
}
