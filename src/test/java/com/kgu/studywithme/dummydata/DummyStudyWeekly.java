package com.kgu.studywithme.dummydata;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

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
                Timestamp.valueOf(LocalDateTime.now().minusDays(3)),
                randomEndDate(),
                1,
                1
        );
    }

    private static Timestamp randomEndDate() {
        final int year = ThreadLocalRandom.current().nextInt(2023, 2024 + 1);
        final int month = (year == 2023)
                ? (ThreadLocalRandom.current().nextInt(11, 12 + 1))
                : (ThreadLocalRandom.current().nextInt(1, 12 + 1));
        final int day = (year == 2023)
                ? ((month == 11) ? (ThreadLocalRandom.current().nextInt(10, 30 + 1)) : (ThreadLocalRandom.current().nextInt(1, 31 + 1)))
                : ThreadLocalRandom.current().nextInt(1, 28 + 1);

        final LocalDateTime dateTime = LocalDateTime.of(year, month, day, 0, 0, 0);
        return Timestamp.valueOf(dateTime);
    }
}
