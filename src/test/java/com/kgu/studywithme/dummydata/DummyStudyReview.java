package com.kgu.studywithme.dummydata;

public record DummyStudyReview(
        long studyId, long writerId, String content
) {
    public DummyStudyReview(final long studyId, final long writerId) {
        this(studyId, writerId, "스터디 리뷰" + writerId);
    }
}
