package com.kgu.studywithme.studynotice.infrastructure.repository.query;

public interface StudyNoticeHandlingRepository {
    long updateNotice(final Long noticeId, final String title, final String content);

    void deleteNotice(final Long noticeId);
}
