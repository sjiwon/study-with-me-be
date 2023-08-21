package com.kgu.studywithme.dummydata;

public record DummyStudyNoticeComment(
        long noticeId, long writerId, String content
) {
    public DummyStudyNoticeComment(final long noticeId, final long writerId) {
        this(noticeId, writerId, "댓글" + noticeId);
    }
}
