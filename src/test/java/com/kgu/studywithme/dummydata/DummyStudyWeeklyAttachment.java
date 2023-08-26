package com.kgu.studywithme.dummydata;

public record DummyStudyWeeklyAttachment(
        long weekId, String link, String uploadFileName
) {
    public DummyStudyWeeklyAttachment(final long weekId) {
        this(weekId, "https://notion.so/" + weekId, "hello" + weekId + ".pdf");
    }
}
