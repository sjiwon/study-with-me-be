package com.kgu.studywithme.dummydata;

import com.kgu.studywithme.study.domain.model.RecruitmentStatus;

public record DummyStudy(
        long hostId, String name, String description, String category,
        int capacity, int participants, String thumbnail,
        String studyType, String province, String city,
        String recruitmentStatus, int minimumAttendance,
        int policyUpdateChance, int isTerminated
) {
    public DummyStudy(
            final int i,
            final long hostId,
            final String category,
            final String thumbnail,
            final String studyType
    ) {
        this(
                1,
                "스터디명" + i,
                "스터디 설명" + i,
                category,
                10,
                5,
                thumbnail,
                studyType,
                (studyType.equals("ONLINE")) ? null : "경기도",
                (studyType.equals("ONLINE")) ? null : "안양시",
                RecruitmentStatus.ON.name(),
                0,
                3,
                0
        );
    }
}
