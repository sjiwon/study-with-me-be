package com.kgu.studywithme.study.application.usecase.command;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.study.domain.model.StudyThumbnail;
import com.kgu.studywithme.study.domain.model.StudyType;

import java.util.Set;

public interface UpdateStudyUseCase {
    void invoke(final Command command);

    record Command(
            Long studyId,
            String name,
            String description,
            Category category,
            int capacity,
            StudyThumbnail thumbnail,
            StudyType type,
            String province,
            String city,
            boolean recruitmentStatus,
            int minimumAttendanceForGraduation,
            Set<String> hashtags
    ) {
    }
}
