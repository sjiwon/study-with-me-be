package com.kgu.studywithme.studynotice.application.adapter;

public interface StudyNoticeHandlingRepositoryAdapter {
    long updateNotice(final Long noticeId, final String title, final String content);

    void deleteNotice(final Long noticeId);
}
