package com.kgu.studywithme.dummydata;

public record DummyStudyNotice(
        long studyId, long writerId,
        String title, String content
) {
    public DummyStudyNotice(final long studyId, final long writerId) {
        this(
                studyId, writerId,
                "제목" + studyId, "내용" + studyId
        );
    }
}
