package com.kgu.studywithme.studynotice.domain.repository;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyNoticeRepository extends JpaRepository<StudyNotice, Long> {
    default StudyNotice getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> StudyWithMeException.type(StudyNoticeErrorCode.NOTICE_NOT_FOUND));
    }

    // @Query
    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE StudyNotice sn" +
            " SET sn.title = :title, sn.content = :content" +
            " WHERE sn.id = :noticeId")
    void update(@Param("noticeId") final Long noticeId, @Param("title") final String title, @Param("content") final String content);
}
