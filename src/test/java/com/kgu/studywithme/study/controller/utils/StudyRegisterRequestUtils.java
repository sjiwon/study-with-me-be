package com.kgu.studywithme.study.controller.utils;

import com.kgu.studywithme.study.controller.dto.request.StudyRegisterRequest;

import static com.kgu.studywithme.fixture.StudyFixture.TOEIC;
import static com.kgu.studywithme.fixture.StudyFixture.TOSS_INTERVIEW;

public class StudyRegisterRequestUtils {
    public static StudyRegisterRequest createOnlineStudyRegisterRequest() {
        return StudyRegisterRequest.builder()
                .name(TOEIC.getName())
                .description(TOEIC.getDescription())
                .category(TOEIC.getCategory().getId())
                .capacity(TOEIC.getCapacity())
                .thumbnail(TOEIC.getThumbnail().getImageName())
                .type(TOEIC.getType().getBrief())
                .minimumAttendanceForGraduation(TOEIC.getMinimumAttendanceForGraduation())
                .hashtags(TOEIC.getHashtags())
                .build();
    }

    public static StudyRegisterRequest createOfflineStudyRegisterRequest() {
        return StudyRegisterRequest.builder()
                .name(TOSS_INTERVIEW.getName())
                .description(TOSS_INTERVIEW.getDescription())
                .category(TOSS_INTERVIEW.getCategory().getId())
                .capacity(TOSS_INTERVIEW.getCapacity())
                .thumbnail(TOSS_INTERVIEW.getThumbnail().getImageName())
                .type(TOSS_INTERVIEW.getType().getBrief())
                .province(TOSS_INTERVIEW.getLocation().getProvince())
                .city(TOSS_INTERVIEW.getLocation().getCity())
                .minimumAttendanceForGraduation(TOSS_INTERVIEW.getMinimumAttendanceForGraduation())
                .hashtags(TOSS_INTERVIEW.getHashtags())
                .build();
    }
}
