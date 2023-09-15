package com.kgu.studywithme.dummydata;

import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.FILE;

public record DummyStudyWeeklySubmit(
        long weekId, long participantId,
        String submitType, String uploadFileName, String link
) {
    public DummyStudyWeeklySubmit(final long weekId, final long participantId) {
        this(
                weekId,
                participantId,
                FILE.name(),
                "hello" + participantId + ".pdf",
                "https://notion.so/" + participantId
        );
    }
}
