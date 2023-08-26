package com.kgu.studywithme.dummydata;

import java.sql.Timestamp;

public record DummyStudyWeekly(
        long studyId, long creatorid, int week,
        String title, String content,
        Timestamp startDate, Timestamp endDate,
        int isAssignmentExists, int isAutoAttendance
) {
    public DummyStudyWeekly(final long studyId, final long creatorId) {
        this(
                studyId, creatorId, 1,
                "제목" + creatorId, "내용" + creatorId,
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365)),
                1,
                1
        );
    }
}
