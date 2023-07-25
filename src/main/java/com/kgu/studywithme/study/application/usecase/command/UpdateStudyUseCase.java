package com.kgu.studywithme.study.application.usecase.command;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.domain.*;

import java.util.Set;

public interface UpdateStudyUseCase {
    void updateStudy(final Command command);

    record Command(
            Long studyId,
            StudyName name,
            Description description,
            Category category,
            Capacity capacity,
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
