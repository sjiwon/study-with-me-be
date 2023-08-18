package com.kgu.studywithme.studynotice.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studynotice.application.adapter.StudyNoticeHandlingRepositoryAdapter;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import static com.kgu.studywithme.studynotice.domain.QStudyNotice.studyNotice;
import static com.kgu.studywithme.studynotice.domain.comment.QStudyNoticeComment.studyNoticeComment;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyNoticeHandlingRepository implements StudyNoticeHandlingRepositoryAdapter {
    private final JPAQueryFactory query;

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Override
    public long updateNotice(final Long noticeId, final String title, final String content) {
        return query
                .update(studyNotice)
                .set(studyNotice.title, title)
                .set(studyNotice.content, content)
                .where(studyNotice.id.eq(noticeId))
                .execute();
    }

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Override
    public void deleteNotice(final Long noticeId) {
        // 1. delete notice comment
        query.delete(studyNoticeComment)
                .where(studyNoticeComment.notice.id.eq(noticeId))
                .execute();


        // 2. delete notice
        query.delete(studyNotice)
                .where(studyNotice.id.eq(noticeId))
                .execute();
    }
}
