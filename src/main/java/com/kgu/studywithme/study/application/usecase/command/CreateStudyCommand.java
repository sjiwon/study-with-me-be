package com.kgu.studywithme.study.application.usecase.command;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.study.domain.model.Capacity;
import com.kgu.studywithme.study.domain.model.Description;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.model.StudyLocation;
import com.kgu.studywithme.study.domain.model.StudyName;
import com.kgu.studywithme.study.domain.model.StudyThumbnail;
import com.kgu.studywithme.study.domain.model.StudyType;

import java.util.Set;

import static com.kgu.studywithme.study.domain.model.StudyType.ONLINE;

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
    public Study toDomain() {
        if (type == ONLINE) {
            return Study.createOnlineStudy(
                    hostId,
                    name,
                    description,
                    category,
                    capacity,
                    thumbnail,
                    minimumAttendanceForGraduation,
                    hashtags
            );
        }

        return Study.createOfflineStudy(
                hostId,
                name,
                description,
                category,
                capacity,
                thumbnail,
                new StudyLocation(province, city),
                minimumAttendanceForGraduation,
                hashtags
        );
    }
}
